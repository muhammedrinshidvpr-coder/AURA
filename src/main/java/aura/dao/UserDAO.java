package aura.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import aura.config.DatabaseConfig;
import aura.enums.Role;
import aura.model.User;

/** CRUD on {@code users}; lookup by email for login. */
public class UserDAO {

    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, name, email, password_hash, role FROM users WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT user_id, name, email, password_hash, role FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            Role.valueOf(rs.getString("role"))
        );
    }
}
