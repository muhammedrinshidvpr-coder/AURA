package aura.model;

import aura.enums.Category;
import aura.enums.Priority;
import aura.enums.SubmissionStatus;
import aura.enums.SubmissionType;
import java.time.LocalDateTime;

/**
 * Campus issue or suggestion.
 * CRITICAL ANONYMITY LAW: This entity contains NO studentId field.
 * Submissions are completely decoupled from student identities.
 */
public class Submission {
    private int submissionId;
    private String title;
    private String description;
    private SubmissionType type;
    private Category category;
    private String location;
    private Priority priority;
    private SubmissionStatus status;
    private String photoUrl;
    private int hypeCount;
    private LocalDateTime createdAt;

    public Submission() {
        this.status = SubmissionStatus.PENDING;
        this.priority = Priority.MEDIUM;
        this.hypeCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public Submission(int submissionId, String title, String description, SubmissionType type,
                      Category category, String location, Priority priority) {
        this.submissionId = submissionId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.location = location;
        this.priority = priority;
        this.status = SubmissionStatus.PENDING;
        this.hypeCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SubmissionType getType() {
        return type;
    }

    public void setType(SubmissionType type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getHypeCount() {
        return hypeCount;
    }

    public void setHypeCount(int hypeCount) {
        this.hypeCount = hypeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
