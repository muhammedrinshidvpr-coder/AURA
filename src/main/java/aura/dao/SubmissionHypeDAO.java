package aura.dao;

/**
 * SubmissionHypeDAO — stores individual hype/upvote records and provides counts.
 * Assigned to: Nirmal Binoy (B25CS052)
 *
 * Implement JDBC-based methods using PreparedStatements.
 */
public class SubmissionHypeDAO {

    public SubmissionHypeDAO() {}

    public void addHype(int submissionId, int studentId) {
        // TODO: INSERT INTO submission_hype (submission_id, student_id, created_at) VALUES (?, ?, now())
        throw new UnsupportedOperationException("addHype() not implemented yet");
    }

    public void removeHype(int submissionId, int studentId) {
        // TODO: DELETE FROM submission_hype WHERE submission_id = ? AND student_id = ?
        throw new UnsupportedOperationException("removeHype() not implemented yet");
    }

    public int countHypes(int submissionId) {
        // TODO: SELECT COUNT(*) FROM submission_hype WHERE submission_id = ?
        throw new UnsupportedOperationException("countHypes() not implemented yet");
    }

    public boolean hasStudentHyped(int submissionId, int studentId) {
        // TODO: SELECT 1 FROM submission_hype WHERE submission_id = ? AND student_id = ? LIMIT 1
        throw new UnsupportedOperationException("hasStudentHyped() not implemented yet");
    }
}
