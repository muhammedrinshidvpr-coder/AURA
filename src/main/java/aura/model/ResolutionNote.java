package aura.model;

import java.time.LocalDateTime;

/** One admin note attached to a submission at resolution time. */
public class ResolutionNote {

    private final Integer noteId;
    private final int submissionId;
    private final int adminId;
    private final String note;
    private final LocalDateTime createdAt;

    public ResolutionNote(Integer noteId, int submissionId, int adminId, String note, LocalDateTime createdAt) {
        this.noteId = noteId;
        this.submissionId = submissionId;
        this.adminId = adminId;
        this.note = note;
        this.createdAt = createdAt;
    }

    public Integer getNoteId() {
        return noteId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public int getAdminId() {
        return adminId;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
