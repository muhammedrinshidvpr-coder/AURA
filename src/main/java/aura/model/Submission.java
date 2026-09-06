package aura.model;

import java.time.LocalDateTime;

/**
 * Minimal Submission POJO.
 */
public class Submission {
    private int submissionId;
    private String title;
    private String description;
    private String status;
    private int hypeCount;
    private LocalDateTime createdAt;

    public int getSubmissionId() { return submissionId; }
    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getHypeCount() { return hypeCount; }
    public void setHypeCount(int hypeCount) { this.hypeCount = hypeCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
