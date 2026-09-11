package aura.model;

import java.time.LocalDateTime;

/**
 * Trending upvote cast by a student on a campus submission.
 * Enforces one vote per student per submission.
 */
public class SubmissionHype {
    private int hypeId;
    private int submissionId;
    private int studentId;
    private LocalDateTime createdAt;

    public SubmissionHype() {
        this.createdAt = LocalDateTime.now();
    }

    public SubmissionHype(int hypeId, int submissionId, int studentId) {
        this.hypeId = hypeId;
        this.submissionId = submissionId;
        this.studentId = studentId;
        this.createdAt = LocalDateTime.now();
    }

    public int getHypeId() {
        return hypeId;
    }

    public void setHypeId(int hypeId) {
        this.hypeId = hypeId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
