package aura.util;

/**
 * PasswordUtil — wrapper for password hashing & verification (jBCrypt recommended).
 * Assigned to: Mohammed Nafih (B25CS037)
 *
 * NOTE: Do not store plaintext passwords. Use BCrypt with a work factor of 10+.
 */
public final class PasswordUtil {

    private PasswordUtil() {}

    public static String hashPassword(String plaintext) {
        // TODO: integrate jBCrypt: return BCrypt.hashpw(plaintext, BCrypt.gensalt(12));
        throw new UnsupportedOperationException("hashPassword() not implemented yet");
    }

    public static boolean verifyPassword(String plaintext, String hashed) {
        // TODO: integrate jBCrypt: return BCrypt.checkpw(plaintext, hashed);
        throw new UnsupportedOperationException("verifyPassword() not implemented yet");
    }
}
