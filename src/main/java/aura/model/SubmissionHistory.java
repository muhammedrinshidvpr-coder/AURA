package aura.model;

import java.time.LocalDateTime;

/** One status-transition audit record for a submission. */
public class SubmissionHistory {

    private final Integer historyId;
    private final int submissionId;
    private final String oldStatus;
    private final String newStatus;
    private final int changedBy;
    private final LocalDateTime changedAt;

    public SubmissionHistory(Integer historyId, int submissionId, String oldStatus, String newStatus,
                              int changedBy, LocalDateTime changedAt) {
        this.historyId = historyId;
        this.submissionId = submissionId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public Integer getHistoryId() {
        return historyId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public int getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}
