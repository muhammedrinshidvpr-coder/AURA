package aura.util;

import java.util.regex.Pattern;

/**
 * Institutional input validation constraints.
 * Enforces @tkmce.ac.in domain requirement and input bounds.
 */
public final class ValidationUtil {
    private static final Pattern TKMCE_EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@tkmce\\.ac\\.in$", Pattern.CASE_INSENSITIVE);

    private ValidationUtil() {}

    public static boolean isValidTkmceEmail(String email) {
        if (email == null) return false;
        return TKMCE_EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static void validateSubmission(String title, String description, String location) {
        if (title == null || title.trim().length() < 5) {
            throw new IllegalArgumentException("Title must be at least 5 characters long.");
        }
        if (title.trim().length() > 200) {
            throw new IllegalArgumentException("Title cannot exceed 200 characters.");
        }
        if (description == null || description.trim().length() < 10) {
            throw new IllegalArgumentException("Description must be at least 10 characters long.");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Campus location is required (e.g., 'CSE Lab 3').");
        }
    }
}
