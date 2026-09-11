package aura.service;

import aura.model.User;
import aura.config.OAuthConfig;
import aura.dao.UserDAO;
import aura.util.PasswordUtil;
import aura.util.SessionContext;
import aura.util.ValidationUtil;

import java.sql.SQLException;

/**
 * AuthService — responsibility: authenticate users and expose RBAC checks.
 * Assigned to: Mohammed Nafih (B25CS037)
 *
 * This is a minimal skeleton. Implement BCrypt-based verification and integrate
 * with aura.dao.UserDAO and aura.util.PasswordUtil when available.
 */
public class AuthService {
    private final GoogleOAuthService googleOAuthService;
    private final UserDAO userDAO;

    public AuthService() {
        this(new GoogleOAuthService(), new UserDAO());
    }

    public AuthService(GoogleOAuthService googleOAuthService, UserDAO userDAO) {
        this.googleOAuthService = googleOAuthService;
        this.userDAO = userDAO;
    }

    /**
     * Authenticate credentials and return User on success or null on failure.
     */
    public User authenticate(String email, String password) {
        if (email == null || password == null || userDAO == null) return null;
        if (!validateCredentials(email, password)) return null;
        try {
            User user = userDAO.findByEmail(email);
            if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) return null;
            SessionContext.setCurrentUser(user);
            return user;
        } catch (SQLException | IllegalArgumentException exception) {
            return null;
        }
    }

    /** Creates a password-authenticated student. Administrators must be provisioned separately. */
    public User registerStudent(String name, String email, String password) throws SQLException {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required.");
        if (!ValidationUtil.isValidTkmceEmail(email) || !ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid registration details.");
        }
        if (userDAO.findByEmail(email) != null) throw new IllegalArgumentException("An account already exists for this email.");

        User user = new User();
        user.setName(name.trim());
        user.setEmail(ValidationUtil.normalizeEmail(email));
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setRole("STUDENT");
        userDAO.save(user);
        SessionContext.setCurrentUser(user);
        return user;
    }

    /** Runs Google OAuth, validates the institutional address, and auto-provisions new students. */
    public User loginWithGoogle() throws Exception {
        if (googleOAuthService == null || userDAO == null) throw new IllegalStateException("Google sign-in is unavailable.");
        GoogleOAuthService.GoogleUserInfo profile = googleOAuthService.executeOAuthFlow();
        if (!ValidationUtil.isValidTkmceEmail(profile.email())) {
            throw new SecurityException("Only official @tkmce.ac.in Google accounts are allowed.");
        }
        User user = userDAO.findByEmail(profile.email());
        if (user == null) {
            user = new User();
            user.setName(profile.name().trim());
            user.setEmail(ValidationUtil.normalizeEmail(profile.email()));
            user.setPasswordHash("OAUTH_GOOGLE");
            user.setRole("STUDENT");
            userDAO.save(user);
        }
        SessionContext.setCurrentUser(user);
        return user;
    }

    /** Offline-only login intended for exercising the GUI without Google or database credentials. */
    public User loginMock(String role) {
        if (!OAuthConfig.isDevMode()) throw new SecurityException("Mock login is disabled outside development mode.");
        if (!"STUDENT".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new IllegalArgumentException("Mock role must be STUDENT or ADMIN.");
        }
        User user = new User();
        user.setUserId("ADMIN".equalsIgnoreCase(role) ? -2 : -1);
        user.setName("ADMIN".equalsIgnoreCase(role) ? "Demo Administrator" : "Demo Student");
        user.setEmail("ADMIN".equalsIgnoreCase(role) ? "admin.demo@tkmce.ac.in" : "student.demo@tkmce.ac.in");
        user.setRole(role.toUpperCase());
        SessionContext.setCurrentUser(user);
        return user;
    }

    /**
     * Invalidate session / logout actions for the given userId.
     */
    public void logout(int userId) {
        User currentUser = SessionContext.getCurrentUser();
        if (currentUser != null && currentUser.getUserId() == userId) SessionContext.clearSession();
    }

    public void logout() {
        SessionContext.clearSession();
    }

    public boolean isCurrentUserAdmin() {
        return SessionContext.isAdmin();
    }

    public boolean isCurrentUser(User user) {
        User currentUser = SessionContext.getCurrentUser();
        return user != null && currentUser != null && currentUser.getUserId() == user.getUserId();
    }

    /**
     * Basic credentials validation (format/length). Use ValidationUtil for rules.
     */
    public boolean validateCredentials(String email, String password) {
        return ValidationUtil.isValidTkmceEmail(email) && ValidationUtil.isValidPassword(password);
    }
}
