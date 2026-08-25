package aura.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidationUtilTest {

    @Test
    void acceptsValidTkmceEmails() {
        assertTrue(ValidationUtil.isValidTkmceEmail("asha.menon@tkmce.ac.in"));
        assertTrue(ValidationUtil.isValidTkmceEmail("ASHA.MENON@TKMCE.AC.IN"));
    }

    @Test
    void rejectsNonTkmceEmails() {
        assertFalse(ValidationUtil.isValidTkmceEmail("student@gmail.com"));
        assertFalse(ValidationUtil.isValidTkmceEmail("missing-at-sign"));
        assertFalse(ValidationUtil.isValidTkmceEmail(null));
        assertFalse(ValidationUtil.isValidTkmceEmail(""));
    }

    @Test
    void rejectsDomainNearMisses() {
        assertFalse(ValidationUtil.isValidTkmceEmail("student@tkmce.ac.in.evil.com"));
        assertFalse(ValidationUtil.isValidTkmceEmail("student@notkmce.ac.in"));
        assertFalse(ValidationUtil.isValidTkmceEmail("evil.com/@tkmce.ac.in@gmail.com"));
    }

    @Test
    void titleBoundaryChecks() {
        assertFalse(ValidationUtil.isValidTitle(""));
        assertFalse(ValidationUtil.isValidTitle(null));
        assertTrue(ValidationUtil.isValidTitle("a".repeat(200)));
        assertFalse(ValidationUtil.isValidTitle("a".repeat(201)));
    }

    @Test
    void descriptionBoundaryChecks() {
        assertFalse(ValidationUtil.isValidDescription(""));
        assertTrue(ValidationUtil.isValidDescription("a".repeat(5000)));
        assertFalse(ValidationUtil.isValidDescription("a".repeat(5001)));
    }

    @Test
    void noteBoundaryChecks() {
        assertFalse(ValidationUtil.isValidNote(""));
        assertTrue(ValidationUtil.isValidNote("a".repeat(2000)));
        assertFalse(ValidationUtil.isValidNote("a".repeat(2001)));
    }
}
