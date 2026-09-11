package aura.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtil — wrapper for password hashing & verification (jBCrypt recommended).
 * Assigned to: Mohammed Nafih (B25CS037)
 *
 * NOTE: Do not store plaintext passwords. Use BCrypt with a work factor of 10+.
 */
public final class PasswordUtil {
    private static final int BCRYPT_WORK_FACTOR = 12;

    private PasswordUtil() {}

    public static String hashPassword(String plaintext) {
        if (!ValidationUtil.isValidPassword(plaintext)) {
            throw new IllegalArgumentException("Password must contain at least 8 characters.");
        }
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
    }

    public static boolean verifyPassword(String plaintext, String hashed) {
        if (plaintext == null || hashed == null || hashed.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plaintext, hashed);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
