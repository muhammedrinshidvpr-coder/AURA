package aura.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import aura.dao.SubmissionDAO;
import aura.enums.Priority;
import aura.enums.Role;
import aura.enums.SubmissionType;
import aura.exception.AuraException;
import aura.exception.UnauthorizedActionException;
import aura.model.Submission;
import aura.model.User;
import aura.util.SessionContext;
import aura.util.ValidationUtil;

/** Create a submission, fetch trending/recent lists (no author identity), fetch a single submission's public detail. */
public class SubmissionService {

    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    public int createSubmission(String title, String description, SubmissionType type, Priority priority) {
        requireStudent();
        if (!ValidationUtil.isValidTitle(title)) {
            throw new AuraException("Title must be between 1 and 200 characters.");
        }
        if (!ValidationUtil.isValidDescription(description)) {
            throw new AuraException("Description must be between 1 and 5000 characters.");
        }
        try {
            int studentId = SessionContext.getCurrentUser().getUserId();
            return submissionDAO.insert(studentId, title, description, type, priority);
        } catch (SQLException e) {
            throw new AuraException("Failed to create submission.", e);
        }
    }

    public List<Submission> getTrending() {
        try {
            return submissionDAO.findAllTrending();
        } catch (SQLException e) {
            throw new AuraException("Failed to load trending submissions.", e);
        }
    }

    public List<Submission> getRecent() {
        try {
            return submissionDAO.findAllRecent();
        } catch (SQLException e) {
            throw new AuraException("Failed to load recent submissions.", e);
        }
    }

    public Optional<Submission> getPublicDetail(int submissionId) {
        try {
            return submissionDAO.findPublicDetail(submissionId);
        } catch (SQLException e) {
            throw new AuraException("Failed to load submission detail.", e);
        }
    }

    private void requireStudent() {
        User user = SessionContext.getCurrentUser();
        if (user == null || user.getRole() != Role.STUDENT) {
            throw new UnauthorizedActionException("Only a logged-in student may perform this action.");
        }
    }
}
