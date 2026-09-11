package aura.dao;

import aura.model.Submission;
import aura.model.ResolutionNote;
import aura.model.SubmissionHistory;
import aura.config.DatabaseConfig;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SubmissionDAO — persistence for Submission entities.
 * Assigned to: Rahandeep RD (B25CS053)
 */
public class SubmissionDAO {

    public SubmissionDAO() {}

    public void save(Submission submission) throws SQLException {
        String sql = "INSERT INTO submissions (title, description, type, category, location, priority, status, photo_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindSubmission(statement, submission);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) submission.setSubmissionId(keys.getInt(1));
            }
        }
    }

    public Submission findById(int id) throws SQLException {
        String sql = baseSelect() + " WHERE s.submission_id = ?";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) { return results.next() ? mapSubmission(results) : null; }
        }
    }

    public List<Submission> findByIds(List<Integer> ids) throws SQLException {
        List<Submission> submissions = new ArrayList<>();
        for (Integer id : ids) {
            Submission submission = findById(id);
            if (submission != null) submissions.add(submission);
        }
        return submissions;
    }

    public List<Submission> findAll() throws SQLException {
        String sql = baseSelect() + " ORDER BY hype_count DESC, s.created_at DESC";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet results = statement.executeQuery()) {
            List<Submission> submissions = new ArrayList<>();
            while (results.next()) submissions.add(mapSubmission(results));
            return submissions;
        }
    }

    public void updateStatus(int id, String status) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE submissions SET status = ? WHERE submission_id = ?")) {
            statement.setString(1, status); statement.setInt(2, id); statement.executeUpdate();
        }
    }

    public void updateStatusWithHistory(int id, String oldStatus, String newStatus, int changedBy) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement update = connection.prepareStatement("UPDATE submissions SET status = ? WHERE submission_id = ?");
                 PreparedStatement history = connection.prepareStatement("INSERT INTO submission_history (submission_id, old_status, new_status, changed_by) VALUES (?, ?, ?, ?)")) {
                update.setString(1, newStatus); update.setInt(2, id); update.executeUpdate();
                history.setInt(1, id); history.setString(2, oldStatus); history.setString(3, newStatus); history.setInt(4, changedBy); history.executeUpdate();
                connection.commit();
            } catch (SQLException exception) { connection.rollback(); throw exception; }
        }
    }

    public void createWithReceipt(Submission submission, int studentId) throws SQLException {
        String insertSubmission = "INSERT INTO submissions (title, description, type, category, location, priority, status, photo_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertReceipt = "INSERT INTO student_submission_receipts (student_id, submission_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement insert = connection.prepareStatement(insertSubmission, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement receipt = connection.prepareStatement(insertReceipt)) {
                bindSubmission(insert, submission); insert.executeUpdate();
                try (ResultSet keys = insert.getGeneratedKeys()) { if (!keys.next()) throw new SQLException("Submission ID was not generated."); submission.setSubmissionId(keys.getInt(1)); }
                receipt.setInt(1, studentId); receipt.setInt(2, submission.getSubmissionId()); receipt.executeUpdate();
                connection.commit();
            } catch (SQLException exception) { connection.rollback(); throw exception; }
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM submissions WHERE submission_id = ?")) {
            statement.setInt(1, id); statement.executeUpdate();
        }
    }

    public void addHistory(int submissionId, String oldStatus, String newStatus, int changedBy) throws SQLException {
        String sql = "INSERT INTO submission_history (submission_id, old_status, new_status, changed_by) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, submissionId); statement.setString(2, oldStatus); statement.setString(3, newStatus); statement.setInt(4, changedBy); statement.executeUpdate();
        }
    }

    public void addResolutionNote(ResolutionNote note) throws SQLException {
        String sql = "INSERT INTO resolution_notes (submission_id, admin_id, note) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, note.getSubmissionId()); statement.setInt(2, note.getAdminId()); statement.setString(3, note.getNote()); statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (keys.next()) note.setNoteId(keys.getInt(1)); }
        }
    }

    public List<ResolutionNote> findNotes(int submissionId) throws SQLException {
        String sql = "SELECT note_id, submission_id, admin_id, note, created_at FROM resolution_notes WHERE submission_id = ? ORDER BY created_at";
        List<ResolutionNote> notes = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, submissionId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) { ResolutionNote note = new ResolutionNote(); note.setNoteId(results.getInt("note_id")); note.setSubmissionId(results.getInt("submission_id")); note.setAdminId(results.getInt("admin_id")); note.setNote(results.getString("note")); note.setCreatedAt(results.getTimestamp("created_at").toLocalDateTime()); notes.add(note); }
            }
        }
        return notes;
    }

    public List<SubmissionHistory> findHistory(int submissionId) throws SQLException {
        String sql = "SELECT history_id, submission_id, old_status, new_status, changed_by, changed_at FROM submission_history WHERE submission_id = ? ORDER BY changed_at";
        List<SubmissionHistory> history = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, submissionId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) { SubmissionHistory item = new SubmissionHistory(); item.setHistoryId(results.getInt("history_id")); item.setSubmissionId(results.getInt("submission_id")); item.setOldStatus(results.getString("old_status")); item.setNewStatus(results.getString("new_status")); item.setChangedBy(results.getInt("changed_by")); item.setChangedAt(results.getTimestamp("changed_at").toLocalDateTime()); history.add(item); }
            }
        }
        return history;
    }

    private String baseSelect() {
        return "SELECT s.*, COUNT(h.hype_id) AS hype_count FROM submissions s LEFT JOIN submission_hype h ON h.submission_id = s.submission_id GROUP BY s.submission_id";
    }

    private void bindSubmission(PreparedStatement statement, Submission submission) throws SQLException {
        statement.setString(1, submission.getTitle()); statement.setString(2, submission.getDescription());
        statement.setString(3, submission.getType()); statement.setString(4, submission.getCategory());
        statement.setString(5, submission.getLocation()); statement.setString(6, submission.getPriority());
        statement.setString(7, submission.getStatus() == null ? "PENDING" : submission.getStatus()); statement.setString(8, submission.getPhotoUrl());
    }

    private Submission mapSubmission(ResultSet results) throws SQLException {
        Submission submission = new Submission(); submission.setSubmissionId(results.getInt("submission_id"));
        submission.setTitle(results.getString("title")); submission.setDescription(results.getString("description"));
        submission.setType(results.getString("type")); submission.setCategory(results.getString("category"));
        submission.setLocation(results.getString("location")); submission.setPriority(results.getString("priority"));
        submission.setStatus(results.getString("status")); submission.setPhotoUrl(results.getString("photo_url"));
        submission.setHypeCount(results.getInt("hype_count"));
        if (results.getTimestamp("created_at") != null) submission.setCreatedAt(results.getTimestamp("created_at").toLocalDateTime());
        return submission;
    }
}
