package aura.dao;

import aura.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for student crowd upvotes (Hype Engine).
 * Strictly enforces one upvote per student per submission.
 */
public class SubmissionHypeDAO {

    public boolean addHype(int submissionId, int studentId) {
        String sql = "INSERT INTO submission_hype (submission_id, student_id) VALUES (?, ?) ON CONFLICT (submission_id, student_id) DO NOTHING";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, studentId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error adding hype: " + e.getMessage(), e);
        }
    }

    public boolean removeHype(int submissionId, int studentId) {
        String sql = "DELETE FROM submission_hype WHERE submission_id = ? AND student_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, studentId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error removing hype: " + e.getMessage(), e);
        }
    }

    public boolean hasStudentHyped(int submissionId, int studentId) {
        String sql = "SELECT 1 FROM submission_hype WHERE submission_id = ? AND student_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error checking hype status: " + e.getMessage(), e);
        }
    }

    public int countHypes(int submissionId) {
        String sql = "SELECT COUNT(*) FROM submission_hype WHERE submission_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error counting hypes: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Integer> findHypedSubmissionIdsByStudent(int studentId) {
        String sql = "SELECT submission_id FROM submission_hype WHERE student_id = ?";
        List<Integer> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("submission_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error fetching hyped submissions: " + e.getMessage(), e);
        }
        return list;
    }
}
