package aura.service;

import aura.dao.StudentReceiptDAO;
import aura.dao.SubmissionDAO;
import aura.model.ResolutionNote;
import aura.model.Submission;
import aura.model.SubmissionHistory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseSubmissionServiceTest {
    @Test
    void createsSubmissionAndPrivateReceipt() {
        FakeReceiptDAO receipts = new FakeReceiptDAO();
        FakeSubmissionDAO submissions = new FakeSubmissionDAO(receipts);
        DatabaseSubmissionService service = new DatabaseSubmissionService(submissions, receipts);

        Submission submission = new Submission();
        submission.setTitle("Broken projector");
        submission.setDescription("The lamp flickers.");
        Submission created = service.createSubmission(submission, 42);

        assertEquals(100, created.getSubmissionId());
        assertEquals("PENDING", created.getStatus());
        assertEquals(42, receipts.studentId);
        assertEquals(100, receipts.submissionId);
    }

    @Test
    void recordsValidStatusChangeAndRejectsInvalidTransition() {
        FakeSubmissionDAO submissions = new FakeSubmissionDAO(new FakeReceiptDAO());
        DatabaseSubmissionService service = new DatabaseSubmissionService(submissions, new FakeReceiptDAO());

        service.updateStatus(100, "ASSIGNED", 7);
        assertEquals("ASSIGNED", submissions.submission.getStatus());
        assertEquals("PENDING", submissions.history.get(0).getOldStatus());

        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(100, "RESOLVED", 7));
    }

    @Test
    void persistsResolutionNote() {
        FakeSubmissionDAO submissions = new FakeSubmissionDAO(new FakeReceiptDAO());
        DatabaseSubmissionService service = new DatabaseSubmissionService(submissions, new FakeReceiptDAO());

        ResolutionNote note = service.addResolutionNote(100, 7, "Assigned to maintenance.");

        assertEquals("Assigned to maintenance.", note.getNote());
        assertSame(note, submissions.notes.get(0));
    }

    private static class FakeSubmissionDAO extends SubmissionDAO {
        private final Submission submission = submission();
        private final FakeReceiptDAO receipts;
        private final List<SubmissionHistory> history = new ArrayList<>();
        private final List<ResolutionNote> notes = new ArrayList<>();

        private FakeSubmissionDAO(FakeReceiptDAO receipts) { this.receipts = receipts; }
        @Override public void createWithReceipt(Submission value, int studentId) { value.setSubmissionId(100); receipts.studentId = studentId; receipts.submissionId = 100; }
        @Override public Submission findById(int id) { return id == 100 ? submission : null; }
        @Override public void updateStatusWithHistory(int id, String oldStatus, String newStatus, int changedBy) {
            submission.setStatus(newStatus);
            SubmissionHistory item = new SubmissionHistory(); item.setOldStatus(oldStatus); item.setNewStatus(newStatus); history.add(item);
        }
        @Override public void addResolutionNote(ResolutionNote note) { notes.add(note); }
    }

    private static class FakeReceiptDAO extends StudentReceiptDAO {
        private int studentId;
        private int submissionId;
    }

    private static Submission submission() {
        Submission submission = new Submission(); submission.setSubmissionId(100); submission.setStatus("PENDING"); return submission;
    }
}