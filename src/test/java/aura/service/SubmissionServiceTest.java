package aura.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.dao.SubmissionHypeDAO;
import aura.enums.Priority;
import aura.enums.SubmissionStatus;
import aura.enums.SubmissionType;
import aura.exception.AuraException;
import aura.exception.UnauthorizedActionException;
import aura.model.Submission;
import aura.model.User;
import aura.support.TestDatabaseSupport;
import aura.support.TestUserSupport;
import aura.util.SessionContext;

class SubmissionServiceTest {

    private final SubmissionService submissionService = new SubmissionService();
    private final SubmissionHypeDAO hypeDAO = new SubmissionHypeDAO();

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
    void createSubmissionDefaultsToPending() throws SQLException {
        SessionContext.setCurrentUser(TestUserSupport.createStudent("student@tkmce.ac.in"));

        int submissionId = submissionService.createSubmission(
            "Wi-Fi drops", "Wi-Fi drops in Block B", SubmissionType.ISSUE, Priority.HIGH);

        Submission created = submissionService.getPublicDetail(submissionId).orElseThrow();
        assertEquals(SubmissionStatus.PENDING, created.getStatus());
    }

    @Test
    void createSubmissionRejectsNonStudentSession() throws SQLException {
        SessionContext.setCurrentUser(TestUserSupport.createAdmin("admin@tkmce.ac.in"));

        assertThrows(UnauthorizedActionException.class, () ->
            submissionService.createSubmission("Title", "Description", SubmissionType.ISSUE, Priority.LOW));
    }

    @Test
    void createSubmissionRejectsOversizeTitleAndDescription() throws SQLException {
        SessionContext.setCurrentUser(TestUserSupport.createStudent("student2@tkmce.ac.in"));

        assertThrows(AuraException.class, () ->
            submissionService.createSubmission("a".repeat(201), "valid description", SubmissionType.ISSUE, Priority.LOW));
        assertThrows(AuraException.class, () ->
            submissionService.createSubmission("valid title", "", SubmissionType.ISSUE, Priority.LOW));
    }

    @Test
    void trendingSortOrdersByHypeCount() throws SQLException {
        User owner = TestUserSupport.createStudent("owner@tkmce.ac.in");
        User voterA = TestUserSupport.createStudent("voter-a@tkmce.ac.in");
        User voterB = TestUserSupport.createStudent("voter-b@tkmce.ac.in");

        SessionContext.setCurrentUser(owner);
        int lessHyped = submissionService.createSubmission("Less hyped", "d1", SubmissionType.SUGGESTION, Priority.LOW);
        int moreHyped = submissionService.createSubmission("More hyped", "d2", SubmissionType.SUGGESTION, Priority.LOW);

        hypeDAO.insert(lessHyped, voterA.getUserId());
        hypeDAO.insert(moreHyped, voterA.getUserId());
        hypeDAO.insert(moreHyped, voterB.getUserId());

        List<Submission> trending = submissionService.getTrending();

        assertEquals("More hyped", trending.get(0).getTitle());
        assertEquals("Less hyped", trending.get(1).getTitle());
    }

    @Test
    void recentSortOrdersByCreatedAt() throws SQLException {
        SessionContext.setCurrentUser(TestUserSupport.createStudent("recent@tkmce.ac.in"));
        submissionService.createSubmission("First", "d1", SubmissionType.ISSUE, Priority.LOW);
        submissionService.createSubmission("Second", "d2", SubmissionType.ISSUE, Priority.LOW);

        List<Submission> recent = submissionService.getRecent();

        assertEquals("Second", recent.get(0).getTitle());
        assertEquals("First", recent.get(1).getTitle());
    }
}
