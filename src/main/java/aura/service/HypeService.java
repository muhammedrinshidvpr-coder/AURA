package aura.service;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import aura.dao.SubmissionHypeDAO;
import aura.enums.Role;
import aura.exception.AuraException;
import aura.exception.DuplicateHypeException;
import aura.exception.UnauthorizedActionException;
import aura.model.User;
import aura.util.SessionContext;

/**
 * Add/remove a hype for the current student on a submission; enforces
 * one-per-student as a second line of defense behind the DB's
 * UNIQUE(submission_id, student_id) constraint (see docs/SECURITY.md section 7).
 */
public class HypeService {

    private final SubmissionHypeDAO hypeDAO = new SubmissionHypeDAO();

    public void addHype(int submissionId) {
        requireStudent();
        try {
            hypeDAO.insert(submissionId, SessionContext.getCurrentUser().getUserId());
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DuplicateHypeException("You have already hyped this submission.");
        } catch (SQLException e) {
            throw new AuraException("Failed to record hype.", e);
        }
    }

    public void removeHype(int submissionId) {
        requireStudent();
        try {
            hypeDAO.delete(submissionId, SessionContext.getCurrentUser().getUserId());
        } catch (SQLException e) {
            throw new AuraException("Failed to remove hype.", e);
        }
    }

    /** Read-only count for display (e.g. next to a submission in a list) — no role check needed. */
    public int getHypeCount(int submissionId) {
        try {
            return hypeDAO.countForSubmission(submissionId);
        } catch (SQLException e) {
            throw new AuraException("Failed to load hype count.", e);
        }
    }

    private void requireStudent() {
        User user = SessionContext.getCurrentUser();
        if (user == null || user.getRole() != Role.STUDENT) {
            throw new UnauthorizedActionException("Only a logged-in student may perform this action.");
        }
    }
}
