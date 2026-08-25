package aura.service;

import java.sql.SQLException;

import aura.dao.UserDAO;
import aura.enums.Role;
import aura.exception.AuraException;
import aura.exception.InvalidCredentialsException;
import aura.model.User;
import aura.util.PasswordUtil;
import aura.util.SessionContext;
import aura.util.ValidationUtil;

/** Register/login: TKMCE-domain validation, password hashing/verification, session creation. */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User register(String name, String email, String plainPassword, Role role) {
        if (!ValidationUtil.isValidTkmceEmail(email)) {
            throw new InvalidCredentialsException("Registration requires a valid @tkmce.ac.in email address.");
        }
        try {
            if (userDAO.findByEmail(email).isPresent()) {
                throw new InvalidCredentialsException("An account with this email is already registered.");
            }
            String passwordHash = PasswordUtil.hash(plainPassword);
            int userId = userDAO.insert(new User(name, email, passwordHash, role));
            User created = new User(userId, name, email, passwordHash, role);
            SessionContext.setCurrentUser(created);
            return created;
        } catch (SQLException e) {
            throw new AuraException("Registration failed due to a database error.", e);
        }
    }

    public User login(String email, String plainPassword) {
        try {
            User user = ValidationUtil.isValidTkmceEmail(email)
                ? userDAO.findByEmail(email).orElse(null)
                : null;
            if (user == null || !PasswordUtil.verify(plainPassword, user.getPasswordHash())) {
                // Same message for "no such account" and "wrong password" — avoids leaking
                // which one it was (account enumeration).
                throw new InvalidCredentialsException("Invalid email or password.");
            }
            SessionContext.setCurrentUser(user);
            return user;
        } catch (SQLException e) {
            throw new AuraException("Login failed due to a database error.", e);
        }
    }

    public void logout() {
        SessionContext.clear();
    }
}
