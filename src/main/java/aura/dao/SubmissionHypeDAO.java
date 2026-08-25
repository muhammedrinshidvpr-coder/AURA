package aura.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import aura.config.DatabaseConfig;

/**
 * Insert a hype (respecting the DB UNIQUE constraint), delete a hype, count
 * hypes per submission. Hype count is always computed via COUNT(*), never
 * cached — see docs/DATABASE.md section 2.
 */
public class SubmissionHypeDAO {

    /** Relies on the DB's UNIQUE(submission_id, student_id) constraint to reject a duplicate. */
    public int insert(int submissionId, int studentId) throws SQLException {
        String sql = "INSERT INTO submission_hype (submission_id, student_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, studentId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public void delete(int submissionId, int studentId) throws SQLException {
        String sql = "DELETE FROM submission_hype WHERE submission_id = ? AND student_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        }
    }

    public int countForSubmission(int submissionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM submission_hype WHERE submission_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public boolean existsHype(int submissionId, int studentId) throws SQLException {
        String sql = "SELECT 1 FROM submission_hype WHERE submission_id = ? AND student_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
