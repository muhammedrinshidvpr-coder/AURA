package aura.service;

import aura.dao.UserDAO;
import aura.enums.Role;
import aura.model.User;
import aura.util.PasswordUtil;
import aura.util.SessionContext;
import aura.util.ValidationUtil;

import java.util.Optional;

/**
 * Handles user authentication, institutional domain validation, and session lifecycle.
 * Supports both modern RFC 8252 Google OAuth 2.0 and institutional BCrypt credentials.
 */
public class AuthService {
    private final UserDAO userDAO;
    private final GoogleOAuthService googleOAuthService;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.googleOAuthService = new GoogleOAuthService();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.googleOAuthService = new GoogleOAuthService();
    }

    public AuthService(UserDAO userDAO, GoogleOAuthService googleOAuthService) {
        this.userDAO = userDAO;
        this.googleOAuthService = googleOAuthService;
    }

    public GoogleOAuthService getGoogleOAuthService() {
        return googleOAuthService;
    }

    /**
     * Authenticates a user using their verified Google Workspace profile.
     * Auto-provisions first-time users as STUDENT in Supabase PostgreSQL while preserving
     * pre-seeded ADMIN privileges.
     */
    public User loginWithGoogle(GoogleOAuthService.GoogleUserInfo info) {
        if (info == null || info.email() == null || info.email().isBlank()) {
            throw new IllegalArgumentException("Google user profile information cannot be empty.");
        }

        String normalizedEmail = info.email().trim().toLowerCase();
        if (!ValidationUtil.isValidTkmceEmail(normalizedEmail)) {
            throw new SecurityException("Institutional Access Denied: Only @tkmce.ac.in Google Workspace accounts are permitted.");
        }

        Optional<User> existingUser = userDAO.findByEmail(normalizedEmail);
        User authenticatedUser;

        if (existingUser.isPresent()) {
            authenticatedUser = existingUser.get();
        } else {
            // Auto-provision new student user (Least Privilege principle)
            String displayName = (info.name() != null && !info.name().isBlank())
                    ? info.name().trim()
                    : "TKMCE Student";

            User newUser = new User(0, displayName, normalizedEmail, "OAUTH_GOOGLE", Role.STUDENT);
            authenticatedUser = userDAO.create(newUser);
        }

        SessionContext.setCurrentUser(authenticatedUser);
        return authenticatedUser;
    }

    public User authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }
        if (!ValidationUtil.isValidTkmceEmail(email)) {
            throw new IllegalArgumentException("Institutional login requires a valid @tkmce.ac.in email.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim());
        if (userOpt.isEmpty()) {
            throw new SecurityException("Invalid credentials. Account not found.");
        }

        User user = userOpt.get();
        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new SecurityException("Invalid credentials. Incorrect password.");
        }

        SessionContext.setCurrentUser(user);
        return user;
    }

    public void logout() {
        SessionContext.clear();
    }
}
