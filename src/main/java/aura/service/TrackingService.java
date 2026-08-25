package aura.service;

import java.sql.SQLException;
import java.util.List;

import aura.dao.ResolutionNoteDAO;
import aura.dao.SubmissionDAO;
import aura.enums.Role;
import aura.exception.AuraException;
import aura.exception.UnauthorizedActionException;
import aura.model.ResolutionNote;
import aura.model.Submission;
import aura.model.User;
import aura.util.SessionContext;

/** findMySubmissions() for the logged-in student only; resolution notes for a student's own submission. */
public class TrackingService {

    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ResolutionNoteDAO resolutionNoteDAO = new ResolutionNoteDAO();

    /** No external studentId parameter — reads the caller's own id from SessionContext. */
    public List<Submission> findMySubmissions() {
        requireStudent();
        try {
            return submissionDAO.findMySubmissions(SessionContext.getCurrentUser().getUserId());
        } catch (SQLException e) {
            throw new AuraException("Failed to load your submissions.", e);
        }
    }

    public List<ResolutionNote> getResolutionNotes(int submissionId) {
        requireStudent();
        boolean isOwnSubmission = findMySubmissions().stream()
            .anyMatch(s -> s.getSubmissionId() == submissionId);
        if (!isOwnSubmission) {
            throw new UnauthorizedActionException("You may only view resolution notes for your own submissions.");
        }
        try {
            return resolutionNoteDAO.findBySubmissionId(submissionId);
        } catch (SQLException e) {
            throw new AuraException("Failed to load resolution notes.", e);
        }
    }

    private void requireStudent() {
        User user = SessionContext.getCurrentUser();
        if (user == null || user.getRole() != Role.STUDENT) {
            throw new UnauthorizedActionException("Only a logged-in student may perform this action.");
        }
    }
}
