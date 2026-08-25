package aura.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.enums.Priority;
import aura.enums.Role;
import aura.enums.SubmissionType;
import aura.model.ResolutionNote;
import aura.model.User;
import aura.support.TestDatabaseSupport;

class ResolutionNoteDAOTest {

    private final UserDAO userDAO = new UserDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ResolutionNoteDAO noteDAO = new ResolutionNoteDAO();

    @BeforeEach
    void resetDatabase() throws SQLException {
        TestDatabaseSupport.resetTables();
    }

    @Test
    void insertThenFindBySubmissionIdRoundTrips() throws SQLException {
        int studentId = userDAO.insert(new User("Student", "student@tkmce.ac.in", "hash", Role.STUDENT));
        int adminId = userDAO.insert(new User("Admin", "admin@tkmce.ac.in", "hash", Role.ADMIN));
        int submissionId = submissionDAO.insert(studentId, "Title", "Desc", SubmissionType.ISSUE, Priority.LOW);

        noteDAO.insert(submissionId, adminId, "Fixed by facilities team.");

        List<ResolutionNote> notes = noteDAO.findBySubmissionId(submissionId);

        assertEquals(1, notes.size());
        assertEquals("Fixed by facilities team.", notes.get(0).getNote());
        assertEquals(adminId, notes.get(0).getAdminId());
        assertEquals(submissionId, notes.get(0).getSubmissionId());
    }
}
