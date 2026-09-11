package aura.dao;

import aura.config.DatabaseConfig;
import aura.model.ResolutionNote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for administrative resolution and progress notes.
 */
public class ResolutionNoteDAO {

    public ResolutionNote create(ResolutionNote note) {
        String sql = "INSERT INTO resolution_notes (submission_id, admin_id, note) VALUES (?, ?, ?) " +
                     "RETURNING note_id, created_at";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, note.getSubmissionId());
            ps.setInt(2, note.getAdminId());
            ps.setString(3, note.getNote());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    note.setNoteId(rs.getInt("note_id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        note.setCreatedAt(ts.toLocalDateTime());
                    }
                    return note;
                }
            }
            throw new SQLException("Failed to retrieve generated note_id");
        } catch (SQLException e) {
            throw new RuntimeException("Database error creating resolution note: " + e.getMessage(), e);
        }
    }

    public List<ResolutionNote> findBySubmissionId(int submissionId) {
        String sql = "SELECT rn.note_id, rn.submission_id, rn.admin_id, COALESCE(u.name, 'Admin') AS admin_name, " +
                     "       rn.note, rn.created_at " +
                     "FROM resolution_notes rn " +
                     "LEFT JOIN users u ON rn.admin_id = u.user_id " +
                     "WHERE rn.submission_id = ? " +
                     "ORDER BY rn.created_at ASC";
        List<ResolutionNote> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResolutionNote note = new ResolutionNote();
                    note.setNoteId(rs.getInt("note_id"));
                    note.setSubmissionId(rs.getInt("submission_id"));
                    note.setAdminId(rs.getInt("admin_id"));
                    note.setAdminName(rs.getString("admin_name"));
                    note.setNote(rs.getString("note"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        note.setCreatedAt(ts.toLocalDateTime());
                    }
                    list.add(note);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding resolution notes: " + e.getMessage(), e);
        }
        return list;
    }
}
