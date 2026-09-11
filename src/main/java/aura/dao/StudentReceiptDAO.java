package aura.dao;

import aura.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentReceiptDAO — manages decoupled anonymity vault receipts.
 * Assigned to: Mohammed Nafih (B25CS037)
 *
 * Minimal skeleton: implement JDBC operations with PreparedStatements.
 */
public class StudentReceiptDAO {

    public StudentReceiptDAO() {
    }

    public void createReceipt(int studentId, int submissionId) throws SQLException {
        if (studentId <= 0 || submissionId <= 0) throw new IllegalArgumentException("Valid student and submission IDs are required.");
        String sql = "INSERT INTO student_submission_receipts (student_id, submission_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, submissionId);
            statement.executeUpdate();
        }
    }

    public List<Integer> findSubmissionIdsByStudent(int studentId) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("A valid student ID is required.");
        String sql = "SELECT submission_id FROM student_submission_receipts WHERE student_id = ? ORDER BY created_at DESC";
        List<Integer> submissionIds = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) submissionIds.add(results.getInt("submission_id"));
            }
        }
        return submissionIds;
    }
}
