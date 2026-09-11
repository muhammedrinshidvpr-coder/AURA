package aura.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password hashing and verification using jBCrypt (work factor 10).
 * Adheres to AGENT.md Rule 4.
 */
public final class PasswordUtil {
    private static final int WORK_FACTOR = 10;

    private PasswordUtil() {}

    public static String hashPassword(String plaintextPassword) {
        if (plaintextPassword == null || plaintextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean checkPassword(String plaintextPassword, String hashedPassword) {
        if (plaintextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plaintextPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
