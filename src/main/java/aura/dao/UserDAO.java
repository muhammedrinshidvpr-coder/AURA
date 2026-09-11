package aura.dao;

import aura.config.DatabaseConfig;
import aura.model.User;
import aura.util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * UserDAO — basic user persistence.
 * Assigned to: Rahandeep RD (B25CS053)
 */
public class UserDAO {

    public UserDAO() {}

    public void save(User user) throws SQLException {
        if (user == null || user.getName() == null || user.getName().isBlank()
                || user.getPasswordHash() == null || user.getPasswordHash().isBlank()
                || user.getRole() == null || user.getRole().isBlank()) {
            throw new IllegalArgumentException("A complete user record is required.");
        }
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName().trim());
            statement.setString(2, ValidationUtil.normalizeEmail(user.getEmail()));
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().trim().toUpperCase());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) user.setUserId(keys.getInt(1));
            }
        }
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT user_id, name, email, password_hash, role FROM users WHERE user_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapUser(results) : null;
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, name, email, password_hash, role FROM users WHERE email = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ValidationUtil.normalizeEmail(email));
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapUser(results) : null;
            }
        }
    }

    public void update(User user) throws SQLException {
        if (user == null || user.getUserId() <= 0) throw new IllegalArgumentException("A persisted user is required.");
        String sql = "UPDATE users SET name = ?, email = ?, password_hash = ?, role = ? WHERE user_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName().trim());
            statement.setString(2, ValidationUtil.normalizeEmail(user.getEmail()));
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().trim().toUpperCase());
            statement.setInt(5, user.getUserId());
            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private User mapUser(ResultSet results) throws SQLException {
        User user = new User();
        user.setUserId(results.getInt("user_id"));
        user.setName(results.getString("name"));
        user.setEmail(results.getString("email"));
        user.setPasswordHash(results.getString("password_hash"));
        user.setRole(results.getString("role"));
        return user;
    }
}
