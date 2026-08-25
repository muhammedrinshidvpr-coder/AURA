package aura.util;

import java.util.regex.Pattern;

/**
 * TKMCE email-domain check, submission title/description length checks — the
 * single source of truth referenced by both aura.ui (fast feedback) and
 * aura.service (the check that actually matters — see docs/AGENT.md rule 6).
 */
public final class ValidationUtil {

    private static final Pattern TKMCE_EMAIL = Pattern.compile("^[^@\\s]+@tkmce\\.ac\\.in$", Pattern.CASE_INSENSITIVE);

    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 5000;
    private static final int MAX_NOTE_LENGTH = 2000;

    private ValidationUtil() {
    }

    public static boolean isValidTkmceEmail(String email) {
        return email != null && TKMCE_EMAIL.matcher(email).matches();
    }

    public static boolean isValidTitle(String title) {
        return title != null && !title.isBlank() && title.length() <= MAX_TITLE_LENGTH;
    }

    public static boolean isValidDescription(String description) {
        return description != null && !description.isBlank() && description.length() <= MAX_DESCRIPTION_LENGTH;
    }

    public static boolean isValidNote(String note) {
        return note != null && !note.isBlank() && note.length() <= MAX_NOTE_LENGTH;
    }
}
