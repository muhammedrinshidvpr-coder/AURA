package aura.service;

import aura.model.Submission;
import aura.model.ResolutionNote;
import aura.model.SubmissionHistory;

import java.util.List;

public interface SubmissionService {
    Submission createSubmission(Submission s, int studentId);
    List<Submission> listAllSubmissions();
    List<Submission> listSubmissionsByStudent(int studentId);
    Submission findById(int submissionId);
    void updateStatus(int submissionId, String newStatus, int changedBy);
    ResolutionNote addResolutionNote(int submissionId, int adminId, String noteText);
    List<ResolutionNote> findNotesBySubmission(int submissionId);
    List<SubmissionHistory> getHistory(int submissionId);
}
