package aura.model;

import java.time.LocalDateTime;

import aura.enums.Priority;
import aura.enums.SubmissionStatus;
import aura.enums.SubmissionType;

/**
 * One reported issue/suggestion.
 *
 * {@code studentId} is nullable by design: every DAO row-mapper used on an
 * admin- or public-facing path leaves it {@code null} because {@code student_id}
 * is never included in those SELECT column lists. Only
 * {@code SubmissionDAO.findMySubmissions} populates it — see
 * docs/ARCHITECTURE.md section 5 (the anonymity boundary).
 */
public class Submission {

    private final Integer submissionId;
    private final Integer studentId;
    private final String title;
    private final String description;
    private final SubmissionType type;
    private final Priority priority;
    private final SubmissionStatus status;
    private final LocalDateTime createdAt;

    public Submission(Integer submissionId, Integer studentId, String title, String description,
                       SubmissionType type, Priority priority, SubmissionStatus status, LocalDateTime createdAt) {
        this.submissionId = submissionId;
        this.studentId = studentId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getSubmissionId() {
        return submissionId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SubmissionType getType() {
        return type;
    }

    public Priority getPriority() {
        return priority;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
