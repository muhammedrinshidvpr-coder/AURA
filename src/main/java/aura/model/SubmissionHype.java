package aura.model;

import java.time.LocalDateTime;

/**
 * One hype (upvote) record: which submission, which voting student, when.
 * {@code studentId} here is the voter — a different identity relationship
 * than {@link Submission#getStudentId()} and not subject to the anonymity
 * rule (see docs/DATABASE.md section 2).
 */
public class SubmissionHype {

    private final Integer hypeId;
    private final int submissionId;
    private final int studentId;
    private final LocalDateTime createdAt;

    public SubmissionHype(Integer hypeId, int submissionId, int studentId, LocalDateTime createdAt) {
        this.hypeId = hypeId;
        this.submissionId = submissionId;
        this.studentId = studentId;
        this.createdAt = createdAt;
    }

    public Integer getHypeId() {
        return hypeId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
