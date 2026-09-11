package aura.model;

import java.time.LocalDateTime;

/**
 * Official administrative note or resolution update.
 */
public class ResolutionNote {
    private int noteId;
    private int submissionId;
    private int adminId;
    private String adminName;
    private String note;
    private LocalDateTime createdAt;

    public ResolutionNote() {
        this.createdAt = LocalDateTime.now();
    }

    public ResolutionNote(int noteId, int submissionId, int adminId, String adminName, String note) {
        this.noteId = noteId;
        this.submissionId = submissionId;
        this.adminId = adminId;
        this.adminName = adminName;
        this.note = note;
        this.createdAt = LocalDateTime.now();
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
