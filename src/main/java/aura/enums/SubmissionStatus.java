package aura.enums;

import java.awt.Color;

/**
 * ============================================================================
 * 5-STATE SUBMISSION LIFECYCLE ENUM
 * ============================================================================
 * Defines the progression states of campus infrastructure reports and suggestions.
 * Colors are tailored for the Modern Minimalist White Theme with high contrast
 * meeting WCAG AA standards (4.5:1+ text-to-background ratio).
 * ============================================================================
 */
public enum SubmissionStatus {
    PENDING("Pending", new Color(0xB4, 0x53, 0x09), new Color(0xFE, 0xF3, 0xC7)),       // Amber 700 / Amber 100
    ASSIGNED("Assigned", new Color(0x03, 0x69, 0xA1), new Color(0xE0, 0xF2, 0xFE)),     // Sky 700 / Sky 100
    IN_PROGRESS("In Progress", new Color(0x43, 0x38, 0xCA), new Color(0xEE, 0xF2, 0xFF)), // Indigo 700 / Indigo 100
    RESOLVED("Resolved", new Color(0x15, 0x80, 0x3D), new Color(0xDC, 0xFC, 0xE7)),     // Green 700 / Green 100
    REJECTED("Rejected", new Color(0xB9, 0x1C, 0x1C), new Color(0xFE, 0xE2, 0xE2));     // Red 700 / Red 100

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
