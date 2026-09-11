package aura.model;

import aura.enums.SubmissionStatus;
import java.time.LocalDateTime;

/**
 * Audit log entry for lifecycle status changes.
 */
public class SubmissionHistory {
    private int historyId;
    private int submissionId;
    private SubmissionStatus oldStatus;
    private SubmissionStatus newStatus;
    private int changedBy;
    private String changedByName;
    private LocalDateTime changedAt;

    public SubmissionHistory() {
        this.changedAt = LocalDateTime.now();
    }

    public SubmissionHistory(int historyId, int submissionId, SubmissionStatus oldStatus,
                             SubmissionStatus newStatus, int changedBy, String changedByName) {
        this.historyId = historyId;
        this.submissionId = submissionId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedByName = changedByName;
        this.changedAt = LocalDateTime.now();
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public SubmissionStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(SubmissionStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public SubmissionStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(SubmissionStatus newStatus) {
        this.newStatus = newStatus;
    }

    public int getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(int changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
