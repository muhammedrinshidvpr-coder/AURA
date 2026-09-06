package aura.util;

/**
 * ValidationUtil — cross-cutting validation rules used across AuthService and UI.
 * Assigned to: Mohammed Nafih (B25CS037)
 */
public final class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        // Very small, conservative check — replace with more complete regex if needed
        return email.endsWith("@tkmce.ac.in") && email.contains("@");
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.length() >= 8; // minimal constraint
    }
}
