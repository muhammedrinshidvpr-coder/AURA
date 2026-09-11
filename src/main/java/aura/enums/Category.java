package aura.enums;

/**
 * University department categories for issue/suggestion routing.
 */
public enum Category {
    IT_INFRASTRUCTURE("IT Infrastructure", "💻"),
    ELECTRICAL("Electrical", "⚡"),
    CIVIL_MAINTENANCE("Civil Maintenance", "🛠️"),
    ACADEMIC_LABS("Academic Labs", "🔬"),
    HOSTEL_MESS("Hostel & Mess", "🍲"),
    GENERAL("General", "🏛️");

    private final String displayName;
    private final String icon;

    Category(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
