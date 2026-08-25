package aura.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import aura.dao.ResolutionNoteDAO;
import aura.dao.SubmissionDAO;
import aura.dao.SubmissionHistoryDAO;
import aura.dao.SubmissionHypeDAO;
import aura.enums.Role;
import aura.enums.SubmissionStatus;
import aura.exception.AuraException;
import aura.exception.UnauthorizedActionException;
import aura.model.Submission;
import aura.model.User;
import aura.util.SessionContext;
import aura.util.ValidationUtil;

/**
 * Admin-only: queue view (no author identity), assign, update status, attach
 * resolution note. Re-validates the ADMIN role on every call (see docs/AGENT.md rule 6).
 *
 * Status pipeline is strict linear: PENDING -> ASSIGNED -> IN_PROGRESS -> {RESOLVED, REJECTED}.
 * No other transition is legal.
 */
public class AdminSubmissionService {

    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final SubmissionHistoryDAO historyDAO = new SubmissionHistoryDAO();
    private final ResolutionNoteDAO noteDAO = new ResolutionNoteDAO();
    private final SubmissionHypeDAO hypeDAO = new SubmissionHypeDAO();

    /** Default order: most recent first (FR-7's "date" sort). */
    public List<Submission> getQueue() {
        requireAdmin();
        try {
            return submissionDAO.findAllForAdminQueue();
        } catch (SQLException e) {
            throw new AuraException("Failed to load the admin queue.", e);
        }
    }

    /** FR-7's "priority" sort: HIGH first. */
    public List<Submission> getQueueSortedByPriority() {
        List<Submission> queue = new ArrayList<>(getQueue());
        queue.sort(Comparator.comparingInt((Submission s) -> s.getPriority().ordinal()).reversed());
        return queue;
    }

    /** FR-7's "hype" sort: most-hyped first. */
    public List<Submission> getQueueSortedByHype() {
        List<Submission> queue = new ArrayList<>(getQueue());
        queue.sort(Comparator.comparingInt((Submission s) -> hypeCount(s.getSubmissionId())).reversed());
        return queue;
    }

    /** PENDING -> ASSIGNED. */
    public void assign(int submissionId) {
        updateStatus(submissionId, SubmissionStatus.ASSIGNED);
    }

    public void updateStatus(int submissionId, SubmissionStatus newStatus) {
        requireAdmin();
        Submission current = getForAdmin(submissionId);
        validateTransition(current.getStatus(), newStatus);
        try {
            submissionDAO.updateStatus(submissionId, newStatus);
            historyDAO.insert(submissionId, current.getStatus(), newStatus, SessionContext.getCurrentUser().getUserId());
        } catch (SQLException e) {
            throw new AuraException("Failed to update submission status.", e);
        }
    }

    public void addResolutionNote(int submissionId, String note) {
        requireAdmin();
        if (!ValidationUtil.isValidNote(note)) {
            throw new AuraException("Resolution note must be between 1 and 2000 characters.");
        }
        try {
            noteDAO.insert(submissionId, SessionContext.getCurrentUser().getUserId(), note);
        } catch (SQLException e) {
            throw new AuraException("Failed to add resolution note.", e);
        }
    }

    public Map<SubmissionStatus, Integer> getDashboardStats() {
        List<Submission> queue = getQueue();
        Map<SubmissionStatus, Integer> stats = new EnumMap<>(SubmissionStatus.class);
        for (SubmissionStatus status : SubmissionStatus.values()) {
            stats.put(status, 0);
        }
        for (Submission submission : queue) {
            stats.merge(submission.getStatus(), 1, Integer::sum);
        }
        return stats;
    }

    private Submission getForAdmin(int submissionId) {
        try {
            return submissionDAO.findByIdForAdmin(submissionId)
                .orElseThrow(() -> new AuraException("Submission not found: " + submissionId));
        } catch (SQLException e) {
            throw new AuraException("Failed to load submission.", e);
        }
    }

    private int hypeCount(int submissionId) {
        try {
            return hypeDAO.countForSubmission(submissionId);
        } catch (SQLException e) {
            throw new AuraException("Failed to load hype count.", e);
        }
    }

    private void validateTransition(SubmissionStatus current, SubmissionStatus next) {
        boolean legal = switch (current) {
            case PENDING -> next == SubmissionStatus.ASSIGNED;
            case ASSIGNED -> next == SubmissionStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == SubmissionStatus.RESOLVED || next == SubmissionStatus.REJECTED;
            case RESOLVED, REJECTED -> false;
        };
        if (!legal) {
            throw new AuraException("Illegal status transition: " + current + " -> " + next);
        }
    }

    private void requireAdmin() {
        User user = SessionContext.getCurrentUser();
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only a logged-in admin may perform this action.");
        }
    }
}
