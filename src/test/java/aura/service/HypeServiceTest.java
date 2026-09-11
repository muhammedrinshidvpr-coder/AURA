package aura.service;

import aura.dao.SubmissionHypeDAO;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HypeServiceTest {
    @Test
    void duplicateStudentHypeDoesNotIncreaseCount() {
        FakeHypeDAO dao = new FakeHypeDAO();
        HypeService service = new HypeService(dao);

        service.addHype(100, 42);
        service.addHype(100, 42);

        assertEquals(1, service.getHypeCount(100));
    }

    private static class FakeHypeDAO extends SubmissionHypeDAO {
        private final Set<String> votes = new HashSet<>();

        @Override public void addHype(int submissionId, int studentId) { votes.add(submissionId + ":" + studentId); }
        @Override public void removeHype(int submissionId, int studentId) { votes.remove(submissionId + ":" + studentId); }
        @Override public int countHypes(int submissionId) { return (int) votes.stream().filter(vote -> vote.startsWith(submissionId + ":")).count(); }
        @Override public boolean hasStudentHyped(int submissionId, int studentId) { return votes.contains(submissionId + ":" + studentId); }
    }
}