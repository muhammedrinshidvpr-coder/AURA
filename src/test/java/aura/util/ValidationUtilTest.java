package aura.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationUtilTest {
    @Test
    void acceptsOnlyInstitutionalEmailAddresses() {
        assertTrue(ValidationUtil.isValidTkmceEmail("student_2026@tkmce.ac.in"));
        assertTrue(ValidationUtil.isValidTkmceEmail("STUDENT@TKMCE.AC.IN"));
        assertFalse(ValidationUtil.isValidTkmceEmail("student@gmail.com"));
        assertFalse(ValidationUtil.isValidTkmceEmail("student@fake.tkmce.ac.in"));
        assertFalse(ValidationUtil.isValidTkmceEmail("@tkmce.ac.in"));
    }

    @Test
    void normalizesValidatedEmailAddresses() {
        assertEquals("student@tkmce.ac.in", ValidationUtil.normalizeEmail(" Student@TKMCE.AC.IN "));
    }
}
