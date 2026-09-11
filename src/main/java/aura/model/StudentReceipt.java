package aura.model;

import java.time.LocalDateTime;

/**
 * Anonymity Vault Receipt.
 * Maps a student to their private submissions so they can track "My Submissions"
 * without exposing their identity on the public submission or to admins.
 */
public class StudentReceipt {
    private int receiptId;
    private int studentId;
    private int submissionId;
    private LocalDateTime createdAt;

    public StudentReceipt() {
        this.createdAt = LocalDateTime.now();
    }

    public StudentReceipt(int receiptId, int studentId, int submissionId) {
        this.receiptId = receiptId;
        this.studentId = studentId;
        this.submissionId = submissionId;
        this.createdAt = LocalDateTime.now();
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
