package aura.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    @Test
    void hashThenVerifySucceedsWithCorrectPassword() {
        String hash = PasswordUtil.hash("correct-horse-battery-staple");

        assertTrue(PasswordUtil.verify("correct-horse-battery-staple", hash));
    }

    @Test
    void verifyFailsWithWrongPassword() {
        String hash = PasswordUtil.hash("correct-horse-battery-staple");

        assertFalse(PasswordUtil.verify("wrong-password", hash));
    }

    @Test
    void hashingTheSameInputTwiceProducesDifferentSaltedHashes() {
        String hash1 = PasswordUtil.hash("same-password");
        String hash2 = PasswordUtil.hash("same-password");

        assertNotEquals(hash1, hash2);
    }
}
