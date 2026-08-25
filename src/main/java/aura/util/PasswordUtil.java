package aura.util;

import org.mindrot.jbcrypt.BCrypt;

/** Wraps jBCrypt hash/verify calls — the only place BCrypt is called directly. */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean verify(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }
}
