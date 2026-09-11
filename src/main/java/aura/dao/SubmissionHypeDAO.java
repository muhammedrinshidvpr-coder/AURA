package aura.dao;

import aura.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SubmissionHypeDAO — stores individual hype/upvote records and provides counts.
 * Assigned to: Nirmal Binoy (B25CS052)
 *
 * Implement JDBC-based methods using PreparedStatements.
 */
public class SubmissionHypeDAO {

    public SubmissionHypeDAO() {}

    public void addHype(int submissionId, int studentId) throws SQLException {
        String sql = "INSERT INTO submission_hype (submission_id, student_id) VALUES (?, ?) ON CONFLICT (submission_id, student_id) DO NOTHING";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) { statement.setInt(1, submissionId); statement.setInt(2, studentId); statement.executeUpdate(); }
    }

    public void removeHype(int submissionId, int studentId) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM submission_hype WHERE submission_id = ? AND student_id = ?")) { statement.setInt(1, submissionId); statement.setInt(2, studentId); statement.executeUpdate(); }
    }

    public int countHypes(int submissionId) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM submission_hype WHERE submission_id = ?")) { statement.setInt(1, submissionId); try (ResultSet results = statement.executeQuery()) { results.next(); return results.getInt(1); } }
    }

    public boolean hasStudentHyped(int submissionId, int studentId) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM submission_hype WHERE submission_id = ? AND student_id = ? LIMIT 1")) { statement.setInt(1, submissionId); statement.setInt(2, studentId); try (ResultSet results = statement.executeQuery()) { return results.next(); } }
    }
}
