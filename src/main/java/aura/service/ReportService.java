package aura.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import aura.dao.ResolutionNoteDAO;
import aura.dao.SubmissionDAO;
import aura.enums.Role;
import aura.exception.AuraException;
import aura.exception.UnauthorizedActionException;
import aura.model.ResolutionNote;
import aura.model.Submission;
import aura.model.User;
import aura.util.SessionContext;

/**
 * Builds the CSV export for admin (FR-11). Pulls only admin-safe data — the
 * same student_id-free query set AdminSubmissionService uses — so the export
 * can never reveal who authored a submission (see docs/PRD.md section 6).
 */
public class ReportService {

    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ResolutionNoteDAO noteDAO = new ResolutionNoteDAO();

    public void exportCsv(Path outputFile) {
        requireAdmin();
        try {
            List<Submission> submissions = submissionDAO.findAllForAdminQueue();
            try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
                writer.write("submission_id,title,type,priority,status,created_at,resolution_notes");
                writer.newLine();
                for (Submission submission : submissions) {
                    writer.write(toCsvRow(submission));
                    writer.newLine();
                }
            }
        } catch (SQLException | IOException e) {
            throw new AuraException("Failed to export CSV report.", e);
        }
    }

    private String toCsvRow(Submission submission) throws SQLException {
        List<ResolutionNote> notes = noteDAO.findBySubmissionId(submission.getSubmissionId());
        String joinedNotes = notes.stream().map(ResolutionNote::getNote).collect(Collectors.joining(" | "));
        return String.join(",",
            String.valueOf(submission.getSubmissionId()),
            escapeCsv(submission.getTitle()),
            submission.getType().name(),
            submission.getPriority().name(),
            submission.getStatus().name(),
            submission.getCreatedAt().toString(),
            escapeCsv(joinedNotes)
        );
    }

    private String escapeCsv(String value) {
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private void requireAdmin() {
        User user = SessionContext.getCurrentUser();
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only a logged-in admin may perform this action.");
        }
    }
}
