package aura.enums;

import java.awt.Color;

/**
 * Urgency levels for campus submissions.
 */
public enum Priority {
    LOW("Low", new Color(59, 130, 246)),
    MEDIUM("Medium", new Color(245, 158, 11)),
    HIGH("High", new Color(239, 68, 68));

    private final String displayName;
    private final Color color;

    Priority(String displayName, Color color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
