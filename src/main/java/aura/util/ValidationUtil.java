package aura.util;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * ValidationUtil — cross-cutting validation rules used across AuthService and UI.
 * Assigned to: Mohammed Nafih (B25CS037)
 */
public final class ValidationUtil {
    private static final Pattern TKMCE_EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@tkmce\\.ac\\.in$", Pattern.CASE_INSENSITIVE);

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        // Very small, conservative check — replace with more complete regex if needed
        return TKMCE_EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isValidTkmceEmail(String email) {
        return isValidEmail(email);
    }

    public static String normalizeEmail(String email) {
        if (!isValidTkmceEmail(email)) {
            throw new IllegalArgumentException("An official @tkmce.ac.in email address is required.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.length() >= 8;
    }
}
