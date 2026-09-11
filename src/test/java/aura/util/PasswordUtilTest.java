package aura.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {
    @Test
    void bcryptHashVerifiesOnlyTheOriginalPassword() {
        String password = "secure-password-123";
        String hash = PasswordUtil.hashPassword(password);

        assertNotEquals(password, hash);
        assertTrue(PasswordUtil.verifyPassword(password, hash));
        assertFalse(PasswordUtil.verifyPassword("wrong-password", hash));
    }

    @Test
    void refusesShortPasswordsAndMalformedHashes() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword("short"));
        assertFalse(PasswordUtil.verifyPassword("valid-password", "not-a-bcrypt-hash"));
    }
}
