package aura.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.dao.SubmissionHistoryDAO;
import aura.enums.Priority;
import aura.enums.SubmissionStatus;
import aura.enums.SubmissionType;
import aura.exception.AuraException;
import aura.exception.UnauthorizedActionException;
import aura.model.Submission;
import aura.model.SubmissionHistory;
import aura.model.User;
import aura.support.TestDatabaseSupport;
import aura.support.TestUserSupport;
import aura.util.SessionContext;

class AdminSubmissionServiceTest {

    private final SubmissionService submissionService = new SubmissionService();
    private final AdminSubmissionService adminService = new AdminSubmissionService();
    private final SubmissionHistoryDAO historyDAO = new SubmissionHistoryDAO();

    @BeforeEach
    void resetDatabase() throws SQLException {
        TestDatabaseSupport.resetTables();
        SessionContext.clear();
    }

    @AfterEach
    void clearSession() {
        SessionContext.clear();
    }

    private int createSubmissionAsStudent(User student) {
        SessionContext.setCurrentUser(student);
        return submissionService.createSubmission("Title", "Description", SubmissionType.ISSUE, Priority.MEDIUM);
    }

    @Test
    void adminQueueNeverExposesStudentId() throws SQLException {
        User student = TestUserSupport.createStudent("owner@tkmce.ac.in");
        createSubmissionAsStudent(student);
        SessionContext.setCurrentUser(TestUserSupport.createAdmin("admin@tkmce.ac.in"));

        List<Submission> queue = adminService.getQueue();

        assertEquals(1, queue.size());
        assertNull(queue.get(0).getStudentId(), "admin queue must never expose studentId");
    }

    @Test
    void legalTransitionPipelineSucceedsAndRecordsHistory() throws SQLException {
        User student = TestUserSupport.createStudent("owner2@tkmce.ac.in");
        int submissionId = createSubmissionAsStudent(student);
        User admin = TestUserSupport.createAdmin("admin2@tkmce.ac.in");
        SessionContext.setCurrentUser(admin);

        adminService.assign(submissionId);
        adminService.updateStatus(submissionId, SubmissionStatus.IN_PROGRESS);
        adminService.addResolutionNote(submissionId, "Fixed.");
        adminService.updateStatus(submissionId, SubmissionStatus.RESOLVED);

        Submission resolved = adminService.getQueue().get(0);
        assertEquals(SubmissionStatus.RESOLVED, resolved.getStatus());

        List<SubmissionHistory> history = historyDAO.findBySubmissionId(submissionId);
        assertEquals(3, history.size());
        assertEquals("RESOLVED", history.get(2).getNewStatus());
    }

    @Test
    void illegalTransitionThrows() throws SQLException {
        User student = TestUserSupport.createStudent("owner3@tkmce.ac.in");
        int submissionId = createSubmissionAsStudent(student);
        SessionContext.setCurrentUser(TestUserSupport.createAdmin("admin3@tkmce.ac.in"));

        assertThrows(AuraException.class, () ->
            adminService.updateStatus(submissionId, SubmissionStatus.RESOLVED));
    }

    @Test
    void adminMethodsRejectStudentSession() throws SQLException {
        SessionContext.setCurrentUser(TestUserSupport.createStudent("student@tkmce.ac.in"));

        assertThrows(UnauthorizedActionException.class, adminService::getQueue);
        assertThrows(UnauthorizedActionException.class, () -> adminService.assign(1));
    }

    @Test
    void queueSortedByPriorityOrdersHighFirst() throws SQLException {
        User student = TestUserSupport.createStudent("owner4@tkmce.ac.in");
        SessionContext.setCurrentUser(student);
        submissionService.createSubmission("Low prio", "d1", SubmissionType.ISSUE, Priority.LOW);
        submissionService.createSubmission("High prio", "d2", SubmissionType.ISSUE, Priority.HIGH);
        SessionContext.setCurrentUser(TestUserSupport.createAdmin("admin4@tkmce.ac.in"));

        List<Submission> sorted = adminService.getQueueSortedByPriority();

        assertTrue(sorted.get(0).getPriority() == Priority.HIGH);
    }
}
