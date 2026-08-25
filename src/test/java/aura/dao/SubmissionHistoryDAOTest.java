package aura.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.enums.Priority;
import aura.enums.Role;
import aura.enums.SubmissionStatus;
import aura.enums.SubmissionType;
import aura.model.SubmissionHistory;
import aura.model.User;
import aura.support.TestDatabaseSupport;

class SubmissionHistoryDAOTest {

    private final UserDAO userDAO = new UserDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final SubmissionHistoryDAO historyDAO = new SubmissionHistoryDAO();

    @BeforeEach
    void resetDatabase() throws SQLException {
        TestDatabaseSupport.resetTables();
    }

    @Test
    void insertThenFindBySubmissionIdRoundTrips() throws SQLException {
        int studentId = userDAO.insert(new User("Student", "student@tkmce.ac.in", "hash", Role.STUDENT));
        int adminId = userDAO.insert(new User("Admin", "admin@tkmce.ac.in", "hash", Role.ADMIN));
        int submissionId = submissionDAO.insert(studentId, "Title", "Desc", SubmissionType.ISSUE, Priority.LOW);

        historyDAO.insert(submissionId, SubmissionStatus.PENDING, SubmissionStatus.ASSIGNED, adminId);
        historyDAO.insert(submissionId, SubmissionStatus.ASSIGNED, SubmissionStatus.IN_PROGRESS, adminId);

        List<SubmissionHistory> history = historyDAO.findBySubmissionId(submissionId);

        assertEquals(2, history.size());
        assertEquals("PENDING", history.get(0).getOldStatus());
        assertEquals("ASSIGNED", history.get(0).getNewStatus());
        assertEquals("IN_PROGRESS", history.get(1).getNewStatus());
        assertEquals(adminId, history.get(0).getChangedBy());
    }
}
