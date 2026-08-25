package aura.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.enums.Priority;
import aura.enums.SubmissionType;
import aura.model.User;
import aura.support.TestDatabaseSupport;
import aura.support.TestUserSupport;
import aura.util.SessionContext;

class ReportServiceTest {

    private final SubmissionService submissionService = new SubmissionService();
    private final ReportService reportService = new ReportService();

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
    void exportedCsvHasHeaderAndOneRowPerSubmissionAndNoStudentIdentity() throws SQLException, IOException {
        User student = TestUserSupport.createStudent("secret.student@tkmce.ac.in");
        SessionContext.setCurrentUser(student);
        submissionService.createSubmission("Wi-Fi drops", "Wi-Fi drops in Block B", SubmissionType.ISSUE, Priority.HIGH);

        SessionContext.setCurrentUser(TestUserSupport.createAdmin("admin@tkmce.ac.in"));
        Path outputFile = Files.createTempFile("aura-report-test", ".csv");

        reportService.exportCsv(outputFile);

        List<String> lines = Files.readAllLines(outputFile);
        assertEquals("submission_id,title,type,priority,status,created_at,resolution_notes", lines.get(0));
        assertEquals(2, lines.size());

        String fileContent = String.join("\n", lines);
        assertFalse(fileContent.contains("secret.student@tkmce.ac.in"), "CSV must never reveal the submitting student's identity");
        assertTrue(fileContent.contains("Wi-Fi drops"));

        Files.deleteIfExists(outputFile);
    }
}
