package aura.enums;

import java.awt.Color;

/**
 * 5-state lifecycle for issues and suggestions.
 */
public enum SubmissionStatus {
    PENDING("Pending", new Color(245, 158, 11), new Color(245, 158, 11, 35)),
    ASSIGNED("Assigned", new Color(56, 189, 248), new Color(56, 189, 248, 35)),
    IN_PROGRESS("In Progress", new Color(129, 140, 248), new Color(129, 140, 248, 35)),
    RESOLVED("Resolved", new Color(16, 185, 129), new Color(16, 185, 129, 35)),
    REJECTED("Rejected", new Color(239, 68, 68), new Color(239, 68, 68, 35));

    private final String displayName;
    private final Color foregroundColor;
    private final Color backgroundColor;

    SubmissionStatus(String displayName, Color foregroundColor, Color backgroundColor) {
        this.displayName = displayName;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
