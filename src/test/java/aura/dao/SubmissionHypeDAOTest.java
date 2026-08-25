package aura.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.enums.Priority;
import aura.enums.Role;
import aura.enums.SubmissionType;
import aura.model.User;
import aura.support.TestDatabaseSupport;

class SubmissionHypeDAOTest {

    private final UserDAO userDAO = new UserDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final SubmissionHypeDAO hypeDAO = new SubmissionHypeDAO();

    @BeforeEach
    void resetDatabase() throws SQLException {
        TestDatabaseSupport.resetTables();
    }

    private int insertSubmission() throws SQLException {
        int studentId = userDAO.insert(new User("Owner", "owner@tkmce.ac.in", "hash", Role.STUDENT));
        return submissionDAO.insert(studentId, "Title", "Desc", SubmissionType.ISSUE, Priority.LOW);
    }

    @Test
    void firstHypeInsertSucceeds() throws SQLException {
        int submissionId = insertSubmission();
        int voterId = userDAO.insert(new User("Voter", "voter@tkmce.ac.in", "hash", Role.STUDENT));

        hypeDAO.insert(submissionId, voterId);

        assertEquals(1, hypeDAO.countForSubmission(submissionId));
        assertTrue(hypeDAO.existsHype(submissionId, voterId));
    }

    @Test
    void duplicateHypeThrowsSqlException() throws SQLException {
        int submissionId = insertSubmission();
        int voterId = userDAO.insert(new User("Voter", "voter@tkmce.ac.in", "hash", Role.STUDENT));
        hypeDAO.insert(submissionId, voterId);

        assertThrows(SQLException.class, () -> hypeDAO.insert(submissionId, voterId));
    }

    @Test
    void deleteRemovesHype() throws SQLException {
        int submissionId = insertSubmission();
        int voterId = userDAO.insert(new User("Voter", "voter@tkmce.ac.in", "hash", Role.STUDENT));
        hypeDAO.insert(submissionId, voterId);

        hypeDAO.delete(submissionId, voterId);

        assertEquals(0, hypeDAO.countForSubmission(submissionId));
        assertFalse(hypeDAO.existsHype(submissionId, voterId));
    }
}
