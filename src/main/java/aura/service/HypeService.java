package aura.service;

import aura.dao.SubmissionHypeDAO;

/**
 * Manages crowd-support upvotes and the trending algorithm.
 * Strictly enforces one vote per student per submission.
 */
public class HypeService {
    private final SubmissionHypeDAO hypeDAO;

    public HypeService() {
        this.hypeDAO = new SubmissionHypeDAO();
    }

    public HypeService(SubmissionHypeDAO hypeDAO) {
        this.hypeDAO = hypeDAO;
    }

    public boolean toggleHype(int submissionId, int studentId) {
        if (hypeDAO.hasStudentHyped(submissionId, studentId)) {
            hypeDAO.removeHype(submissionId, studentId);
            return false;
        } else {
            hypeDAO.addHype(submissionId, studentId);
            return true;
        }
    }

    public boolean hasStudentHyped(int submissionId, int studentId) {
        return hypeDAO.hasStudentHyped(submissionId, studentId);
    }

    public int getHypeCount(int submissionId) {
        return hypeDAO.countHypes(submissionId);
    }
}
