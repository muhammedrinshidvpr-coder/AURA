package aura.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import aura.config.DatabaseConfig;
import aura.model.ResolutionNote;

/** Insert/read resolution notes for a submission. */
public class ResolutionNoteDAO {

    public int insert(int submissionId, int adminId, String note) throws SQLException {
        String sql = "INSERT INTO resolution_notes (submission_id, admin_id, note) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, submissionId);
            ps.setInt(2, adminId);
            ps.setString(3, note);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public List<ResolutionNote> findBySubmissionId(int submissionId) throws SQLException {
        String sql = "SELECT note_id, submission_id, admin_id, note, created_at "
            + "FROM resolution_notes WHERE submission_id = ? ORDER BY created_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ResolutionNote> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(new ResolutionNote(
                        rs.getInt("note_id"),
                        rs.getInt("submission_id"),
                        rs.getInt("admin_id"),
                        rs.getString("note"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
                return results;
            }
        }
    }
}
