package aura.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.enums.Priority;
import aura.enums.SubmissionType;
import aura.exception.DuplicateHypeException;
import aura.exception.UnauthorizedActionException;
import aura.support.TestDatabaseSupport;
import aura.support.TestUserSupport;
import aura.util.SessionContext;

class HypeServiceTest {

    private final SubmissionService submissionService = new SubmissionService();
    private final HypeService hypeService = new HypeService();

    @BeforeEach
    void resetDatabase() throws SQLException {
        TestDatabaseSupport.resetTables();
        SessionContext.clear();
    }

    @AfterEach
    void clearSession() {
        SessionContext.clear();
    }

    private int createSubmission() throws SQLException {
        SessionContext.setCurrentUser(TestUserSupport.createStudent("owner@tkmce.ac.in"));
        return submissionService.createSubmission("Title", "Description", SubmissionType.ISSUE, Priority.LOW);
    }

    @Test
    void firstHypeSucceeds() throws SQLException {
        int submissionId = createSubmission();
        SessionContext.setCurrentUser(TestUserSupport.createStudent("voter@tkmce.ac.in"));

        assertDoesNotThrow(() -> hypeService.addHype(submissionId));
    }

    @Test
    void secondHypeFromSameStudentThrowsDuplicateHypeException() throws SQLException {
        int submissionId = createSubmission();
        SessionContext.setCurrentUser(TestUserSupport.createStudent("voter2@tkmce.ac.in"));
        hypeService.addHype(submissionId);

        assertThrows(DuplicateHypeException.class, () -> hypeService.addHype(submissionId));
    }

    @Test
    void unhypeThenRehypeIsAllowed() throws SQLException {
        int submissionId = createSubmission();
        SessionContext.setCurrentUser(TestUserSupport.createStudent("voter3@tkmce.ac.in"));
        hypeService.addHype(submissionId);

        hypeService.removeHype(submissionId);

        assertDoesNotThrow(() -> hypeService.addHype(submissionId));
    }

    @Test
    void nonStudentSessionCannotHype() throws SQLException {
        int submissionId = createSubmission();
        SessionContext.setCurrentUser(TestUserSupport.createAdmin("admin@tkmce.ac.in"));

        assertThrows(UnauthorizedActionException.class, () -> hypeService.addHype(submissionId));
    }
}
