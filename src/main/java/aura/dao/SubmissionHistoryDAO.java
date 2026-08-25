package aura.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import aura.config.DatabaseConfig;
import aura.enums.SubmissionStatus;
import aura.model.SubmissionHistory;

/** Insert/read status-change audit rows. */
public class SubmissionHistoryDAO {

    public int insert(int submissionId, SubmissionStatus oldStatus, SubmissionStatus newStatus, int changedBy)
            throws SQLException {
        String sql = "INSERT INTO submission_history (submission_id, old_status, new_status, changed_by) "
            + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, submissionId);
            ps.setString(2, oldStatus.name());
            ps.setString(3, newStatus.name());
            ps.setInt(4, changedBy);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public List<SubmissionHistory> findBySubmissionId(int submissionId) throws SQLException {
        String sql = "SELECT history_id, submission_id, old_status, new_status, changed_by, changed_at "
            + "FROM submission_history WHERE submission_id = ? ORDER BY changed_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                List<SubmissionHistory> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(new SubmissionHistory(
                        rs.getInt("history_id"),
                        rs.getInt("submission_id"),
                        rs.getString("old_status"),
                        rs.getString("new_status"),
                        rs.getInt("changed_by"),
                        rs.getTimestamp("changed_at").toLocalDateTime()
                    ));
                }
                return results;
            }
        }
    }
}
