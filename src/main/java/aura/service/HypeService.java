package aura.service;

/**
 * HypeService — manages trending/upvote logic.
 * Assigned to: Nirmal Binoy (B25CS052)
 *
 * Minimal skeleton: must use SubmissionHypeDAO to persist one-vote-per-student semantics
 */
public class HypeService {

    public HypeService() {
    }

    public void addHype(int submissionId, int studentId) {
        // TODO: enforce single-vote-per-student, update aggregated counts
        throw new UnsupportedOperationException("addHype() not implemented yet");
    }

    public void removeHype(int submissionId, int studentId) {
        // TODO: remove a student's hype
        throw new UnsupportedOperationException("removeHype() not implemented yet");
    }

    public int getHypeCount(int submissionId) {
        // TODO: return aggregated count from SubmissionHypeDAO
        throw new UnsupportedOperationException("getHypeCount() not implemented yet");
    }

    public boolean hasStudentHyped(int submissionId, int studentId) {
        // TODO: check DAO
        throw new UnsupportedOperationException("hasStudentHyped() not implemented yet");
    }
}
