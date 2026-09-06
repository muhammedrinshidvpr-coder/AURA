package aura.dao;

import aura.model.Submission;
import java.util.List;

/**
 * SubmissionDAO — persistence for Submission entities.
 * Assigned to: Rahandeep RD (B25CS053)
 */
public class SubmissionDAO {

    public SubmissionDAO() {}

    public void save(Submission submission) {
        // TODO: implement INSERT with generated keys
        throw new UnsupportedOperationException("save() not implemented yet");
    }

    public Submission findById(int id) {
        // TODO: SELECT * FROM submissions WHERE submission_id = ?
        throw new UnsupportedOperationException("findById() not implemented yet");
    }

    public java.util.List<Submission> findByStudent(int studentId) {
        // TODO: fetch submissions by joining receipts table as per Decoupled Anonymity Vault
        throw new UnsupportedOperationException("findByStudent() not implemented yet");
    }

    public java.util.List<Submission> findAll() {
        // TODO: return all public submissions
        throw new UnsupportedOperationException("findAll() not implemented yet");
    }

    public void updateStatus(int id, String status) {
        // TODO: update submission status
        throw new UnsupportedOperationException("updateStatus() not implemented yet");
    }

    public void delete(int id) {
        // TODO: delete submission
        throw new UnsupportedOperationException("delete() not implemented yet");
    }
}
