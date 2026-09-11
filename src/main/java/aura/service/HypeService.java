package aura.service;

import aura.dao.SubmissionHypeDAO;
import java.sql.SQLException;

/**
 * HypeService — manages trending/upvote logic.
 * Assigned to: Nirmal Binoy (B25CS052)
 *
 * Minimal skeleton: must use SubmissionHypeDAO to persist one-vote-per-student semantics
 */
public class HypeService {

    private final SubmissionHypeDAO hypeDAO;

    public HypeService() { this(new SubmissionHypeDAO()); }

    public HypeService(SubmissionHypeDAO hypeDAO) {
        this.hypeDAO = hypeDAO;
    }

    public void addHype(int submissionId, int studentId) {
        try { hypeDAO.addHype(submissionId, studentId); } catch (SQLException exception) { throw new IllegalStateException("Unable to save hype.", exception); }
    }

    public void removeHype(int submissionId, int studentId) {
        try { hypeDAO.removeHype(submissionId, studentId); } catch (SQLException exception) { throw new IllegalStateException("Unable to remove hype.", exception); }
    }

    public int getHypeCount(int submissionId) {
        try { return hypeDAO.countHypes(submissionId); } catch (SQLException exception) { throw new IllegalStateException("Unable to load hype count.", exception); }
    }

    public boolean hasStudentHyped(int submissionId, int studentId) {
        try { return hypeDAO.hasStudentHyped(submissionId, studentId); } catch (SQLException exception) { throw new IllegalStateException("Unable to load hype state.", exception); }
    }
}
