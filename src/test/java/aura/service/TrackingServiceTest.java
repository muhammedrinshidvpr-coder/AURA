package aura.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.dao.ResolutionNoteDAO;
import aura.enums.Priority;
import aura.enums.SubmissionType;
import aura.exception.UnauthorizedActionException;
import aura.model.ResolutionNote;
import aura.model.Submission;
import aura.model.User;
import aura.support.TestDatabaseSupport;
import aura.support.TestUserSupport;
import aura.util.SessionContext;

class TrackingServiceTest {

    private final SubmissionService submissionService = new SubmissionService();
    private final TrackingService trackingService = new TrackingService();
    private final ResolutionNoteDAO noteDAO = new ResolutionNoteDAO();

    @BeforeEach
    void resetDatabase() throws SQLException {
        TestDatabaseSupport.resetTables();
        SessionContext.clear();
    }

    @AfterEach
    void clearSession() {
        SessionContext.clear();
    }

    @Test
    void findMySubmissionsReturnsOnlyOwnSubmissions() throws SQLException {
        User owner = TestUserSupport.createStudent("owner@tkmce.ac.in");
        User other = TestUserSupport.createStudent("other@tkmce.ac.in");

        SessionContext.setCurrentUser(owner);
        submissionService.createSubmission("Mine", "d1", SubmissionType.ISSUE, Priority.LOW);

        SessionContext.setCurrentUser(other);
        submissionService.createSubmission("Not mine", "d2", SubmissionType.ISSUE, Priority.LOW);

        SessionContext.setCurrentUser(owner);
        List<Submission> mine = trackingService.findMySubmissions();

        assertEquals(1, mine.size());
        assertEquals("Mine", mine.get(0).getTitle());
    }

    @Test
    void findMySubmissionsRejectsNoSessionOrAdminSession() throws SQLException {
        SessionContext.clear();
        assertThrows(UnauthorizedActionException.class, trackingService::findMySubmissions);

        SessionContext.setCurrentUser(TestUserSupport.createAdmin("admin@tkmce.ac.in"));
        assertThrows(UnauthorizedActionException.class, trackingService::findMySubmissions);
    }

    @Test
    void getResolutionNotesReturnsNotesForOwnSubmissionOnly() throws SQLException {
        User owner = TestUserSupport.createStudent("owner2@tkmce.ac.in");
        User other = TestUserSupport.createStudent("other2@tkmce.ac.in");
        User admin = TestUserSupport.createAdmin("admin2@tkmce.ac.in");

        SessionContext.setCurrentUser(owner);
        int ownedSubmissionId = submissionService.createSubmission("Mine", "d1", SubmissionType.ISSUE, Priority.LOW);
        noteDAO.insert(ownedSubmissionId, admin.getUserId(), "Resolved by facilities.");

        List<ResolutionNote> notes = trackingService.getResolutionNotes(ownedSubmissionId);
        assertEquals(1, notes.size());
        assertEquals("Resolved by facilities.", notes.get(0).getNote());

        SessionContext.setCurrentUser(other);
        assertThrows(UnauthorizedActionException.class, () -> trackingService.getResolutionNotes(ownedSubmissionId));
    }
}
