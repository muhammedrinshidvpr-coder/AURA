package aura.security;

import aura.model.Submission;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;

/** Guards the public model boundary used by administrative screens. */
class AdminAnonymityRegressionTest {
    @Test
    void publicSubmissionModelDoesNotExposeStudentIdentity() {
        boolean leaksStudentId = Arrays.stream(Submission.class.getMethods())
                .map(Method::getName)
                .anyMatch(name -> name.equalsIgnoreCase("getStudentId") || name.equalsIgnoreCase("setStudentId"));

        assertFalse(leaksStudentId, "Submission must never expose a student ID to administrative code.");
    }
}
