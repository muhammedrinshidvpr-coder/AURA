package aura.service;

import aura.dao.StudentReceiptDAO;
import aura.dao.SubmissionDAO;
import aura.model.ResolutionNote;
import aura.model.Submission;
import aura.model.SubmissionHistory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Database-backed submission workflow. Receipt access is restricted to student queries. */
public class DatabaseSubmissionService implements SubmissionService {
    private static final Map<String, Set<String>> VALID_TRANSITIONS = Map.of(
            "PENDING", Set.of("ASSIGNED", "REJECTED"),
            "ASSIGNED", Set.of("IN_PROGRESS", "REJECTED"),
            "IN_PROGRESS", Set.of("RESOLVED", "REJECTED"),
            "RESOLVED", Collections.emptySet(),
            "REJECTED", Collections.emptySet());

    private final SubmissionDAO submissionDAO;
    private final StudentReceiptDAO receiptDAO;

    public DatabaseSubmissionService() { this(new SubmissionDAO(), new StudentReceiptDAO()); }

    public DatabaseSubmissionService(SubmissionDAO submissionDAO, StudentReceiptDAO receiptDAO) {
        this.submissionDAO = submissionDAO;
        this.receiptDAO = receiptDAO;
    }

    @Override
    public Submission createSubmission(Submission submission, int studentId) {
        if (studentId <= 0) throw new IllegalArgumentException("An authenticated student is required.");
        if (submission == null) throw new IllegalArgumentException("Submission is required.");
        submission.setStatus("PENDING");
        submission.setType(defaultValue(submission.getType(), "ISSUE"));
        submission.setCategory(defaultValue(submission.getCategory(), "GENERAL"));
        submission.setLocation(defaultValue(submission.getLocation(), "Not specified"));
        submission.setPriority(defaultValue(submission.getPriority(), "MEDIUM"));
        try { submissionDAO.createWithReceipt(submission, studentId); return submission; }
        catch (SQLException exception) { throw failure("Unable to create submission.", exception); }
    }

    @Override
    public List<Submission> listAllSubmissions() {
        try { return submissionDAO.findAll(); } catch (SQLException exception) { throw failure("Unable to load submissions.", exception); }
    }

    @Override
    public List<Submission> listSubmissionsByStudent(int studentId) {
        try { return submissionDAO.findByIds(receiptDAO.findSubmissionIdsByStudent(studentId)); }
        catch (SQLException exception) { throw failure("Unable to load your submissions.", exception); }
    }

    @Override
    public Submission findById(int submissionId) {
        try { return submissionDAO.findById(submissionId); } catch (SQLException exception) { throw failure("Unable to load submission.", exception); }
    }

    @Override
    public void updateStatus(int submissionId, String newStatus, int changedBy) {
        if (changedBy <= 0) throw new IllegalArgumentException("An authenticated administrator is required.");
        String normalized = newStatus == null ? "" : newStatus.trim().toUpperCase();
        Submission submission = findById(submissionId);
        if (submission == null) throw new IllegalArgumentException("Submission does not exist.");
        if (!VALID_TRANSITIONS.getOrDefault(submission.getStatus(), Collections.emptySet()).contains(normalized)) {
            throw new IllegalArgumentException("Invalid status transition from " + submission.getStatus() + " to " + normalized + ".");
        }
        try { submissionDAO.updateStatusWithHistory(submissionId, submission.getStatus(), normalized, changedBy); }
        catch (SQLException exception) { throw failure("Unable to update submission status.", exception); }
    }

    @Override
    public ResolutionNote addResolutionNote(int submissionId, int adminId, String noteText) {
        if (adminId <= 0 || noteText == null || noteText.isBlank()) throw new IllegalArgumentException("A valid administrator note is required.");
        ResolutionNote note = new ResolutionNote(); note.setSubmissionId(submissionId); note.setAdminId(adminId); note.setNote(noteText.trim());
        try { submissionDAO.addResolutionNote(note); return note; } catch (SQLException exception) { throw failure("Unable to save resolution note.", exception); }
    }

    @Override
    public List<ResolutionNote> findNotesBySubmission(int submissionId) {
        try { return submissionDAO.findNotes(submissionId); } catch (SQLException exception) { throw failure("Unable to load resolution notes.", exception); }
    }

    @Override
    public List<SubmissionHistory> getHistory(int submissionId) {
        try { return submissionDAO.findHistory(submissionId); } catch (SQLException exception) { throw failure("Unable to load submission history.", exception); }
    }

    private String defaultValue(String value, String fallback) { return value == null || value.isBlank() ? fallback : value.trim().toUpperCase(); }
    private IllegalStateException failure(String message, SQLException exception) { return new IllegalStateException(message, exception); }
}