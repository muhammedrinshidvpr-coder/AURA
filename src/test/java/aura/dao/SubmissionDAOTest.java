package aura.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.enums.Priority;
import aura.enums.Role;
import aura.enums.SubmissionStatus;
import aura.enums.SubmissionType;
import aura.model.Submission;
import aura.model.User;
import aura.support.TestDatabaseSupport;

class SubmissionDAOTest {

    private final UserDAO userDAO = new UserDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final SubmissionHypeDAO hypeDAO = new SubmissionHypeDAO();

    @BeforeEach
    void resetDatabase() throws SQLException {
        TestDatabaseSupport.resetTables();
    }

    private int insertStudent(String email) throws SQLException {
        return userDAO.insert(new User("Test Student", email, "hash", Role.STUDENT));
    }

    @Test
    void findMySubmissionsPopulatesStudentId() throws SQLException {
        int studentId = insertStudent("owner@tkmce.ac.in");
        submissionDAO.insert(studentId, "Wi-Fi drops", "Wi-Fi drops in Block B", SubmissionType.ISSUE, Priority.HIGH);

        List<Submission> mine = submissionDAO.findMySubmissions(studentId);

        assertEquals(1, mine.size());
        assertEquals(studentId, mine.get(0).getStudentId());
        assertEquals("Wi-Fi drops", mine.get(0).getTitle());
        assertEquals(SubmissionStatus.PENDING, mine.get(0).getStatus());
    }

    @Test
    void adminQueueNeverExposesStudentId() throws SQLException {
        int studentId = insertStudent("owner2@tkmce.ac.in");
        submissionDAO.insert(studentId, "Broken projector", "Room 204", SubmissionType.ISSUE, Priority.MEDIUM);

        List<Submission> queue = submissionDAO.findAllForAdminQueue();

        assertEquals(1, queue.size());
        assertNull(queue.get(0).getStudentId(), "admin queue rows must never carry a populated studentId");
    }

    @Test
    void findAllRecentOrdersByCreatedAtDescending() throws SQLException {
        int studentId = insertStudent("recent@tkmce.ac.in");
        submissionDAO.insert(studentId, "First", "first description", SubmissionType.ISSUE, Priority.LOW);
        submissionDAO.insert(studentId, "Second", "second description", SubmissionType.ISSUE, Priority.LOW);

        List<Submission> recent = submissionDAO.findAllRecent();

        assertEquals("Second", recent.get(0).getTitle());
        assertEquals("First", recent.get(1).getTitle());
    }

    @Test
    void findAllTrendingOrdersByHypeCount() throws SQLException {
        int owner = insertStudent("trend-owner@tkmce.ac.in");
        int voterA = insertStudent("voter-a@tkmce.ac.in");
        int voterB = insertStudent("voter-b@tkmce.ac.in");

        int lessHyped = submissionDAO.insert(owner, "Less hyped", "d1", SubmissionType.SUGGESTION, Priority.LOW);
        int moreHyped = submissionDAO.insert(owner, "More hyped", "d2", SubmissionType.SUGGESTION, Priority.LOW);

        hypeDAO.insert(lessHyped, voterA);
        hypeDAO.insert(moreHyped, voterA);
        hypeDAO.insert(moreHyped, voterB);

        List<Submission> trending = submissionDAO.findAllTrending();

        assertEquals("More hyped", trending.get(0).getTitle());
        assertEquals("Less hyped", trending.get(1).getTitle());
    }

    @Test
    void updateStatusMutatesRow() throws SQLException {
        int studentId = insertStudent("status@tkmce.ac.in");
        int submissionId = submissionDAO.insert(studentId, "Title", "Desc", SubmissionType.ISSUE, Priority.MEDIUM);

        boolean updated = submissionDAO.updateStatus(submissionId, SubmissionStatus.ASSIGNED);

        assertTrue(updated);
        Optional<Submission> fetched = submissionDAO.findByIdForAdmin(submissionId);
        assertTrue(fetched.isPresent());
        assertEquals(SubmissionStatus.ASSIGNED, fetched.get().getStatus());
        assertNull(fetched.get().getStudentId());
    }
}
