package aura.dao;

import java.util.List;

/**
 * StudentReceiptDAO — manages decoupled anonymity vault receipts.
 * Assigned to: Mohammed Nafih (B25CS037)
 *
 * Minimal skeleton: implement JDBC operations with PreparedStatements.
 */
public class StudentReceiptDAO {

    public StudentReceiptDAO() {
    }

    public void createReceipt(int studentId, int submissionId) {
        // TODO: insert into student_submission_receipts (receipt_id, student_id, submission_id, created_at)
        throw new UnsupportedOperationException("createReceipt() not implemented yet");
    }

    public java.util.List<Integer> findSubmissionIdsByStudent(int studentId) {
        // TODO: query receipts and return submission IDs
        throw new UnsupportedOperationException("findSubmissionIdsByStudent() not implemented yet");
    }
}
