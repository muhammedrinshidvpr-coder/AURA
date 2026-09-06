package aura.service;

import aura.model.User;
import aura.util.ValidationUtil;

/**
 * AuthService — responsibility: authenticate users and expose RBAC checks.
 * Assigned to: Mohammed Nafih (B25CS037)
 *
 * This is a minimal skeleton. Implement BCrypt-based verification and integrate
 * with aura.dao.UserDAO and aura.util.PasswordUtil when available.
 */
public class AuthService {

    public AuthService() {
    }

    /**
     * Authenticate credentials and return User on success or null on failure.
     */
    public User authenticate(String email, String password) {
        if (email == null || password == null) return null;
        if (!validateCredentials(email, password)) return null;

        // Demo credentials (temporary - replace with DB-backed verification)
        if ("admin@tkmce.ac.in".equalsIgnoreCase(email) && "admin123".equals(password)) {
            User u = new User();
            u.setUserId(1);
            u.setName("Administrator");
            u.setEmail(email);
            u.setRole("ADMIN");
            return u;
        }

        if ("student1@tkmce.ac.in".equalsIgnoreCase(email) && "password123".equals(password)) {
            User u = new User();
            u.setUserId(2);
            u.setName("Student One");
            u.setEmail(email);
            u.setRole("STUDENT");
            return u;
        }

        if ("student2@tkmce.ac.in".equalsIgnoreCase(email) && "password123".equals(password)) {
            User u = new User();
            u.setUserId(3);
            u.setName("Student Two");
            u.setEmail(email);
            u.setRole("STUDENT");
            return u;
        }

        return null;
    }

    /**
     * Invalidate session / logout actions for the given userId.
     */
    public void logout(int userId) {
        // No-op for the demo skeleton. Real implementation should clear session/store.
    }

    /**
     * Basic credentials validation (format/length). Use ValidationUtil for rules.
     */
    public boolean validateCredentials(String email, String password) {
        return ValidationUtil.isValidEmail(email) && ValidationUtil.isValidPassword(password);
    }
}
