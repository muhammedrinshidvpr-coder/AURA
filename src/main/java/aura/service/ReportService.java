package aura.service;

import aura.dao.SubmissionDAO;
import aura.enums.SubmissionStatus;
import aura.model.Submission;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Aggregates administrative KPI metrics and exports compliance reports.
 */
public class ReportService {
    private final SubmissionDAO submissionDAO;

    public ReportService() {
        this.submissionDAO = new SubmissionDAO();
    }

    public ReportService(SubmissionDAO submissionDAO) {
        this.submissionDAO = submissionDAO;
    }

    public int getPendingCount() {
        return (int) submissionDAO.findAll().stream()
                .filter(s -> s.getStatus() == SubmissionStatus.PENDING)
                .count();
    }

    public int getActiveCount() {
        return (int) submissionDAO.findAll().stream()
                .filter(s -> s.getStatus() == SubmissionStatus.ASSIGNED || s.getStatus() == SubmissionStatus.IN_PROGRESS)
                .count();
    }

    public int getResolvedCount() {
        return (int) submissionDAO.findAll().stream()
                .filter(s -> s.getStatus() == SubmissionStatus.RESOLVED)
                .count();
    }

    public int getRejectedCount() {
        return (int) submissionDAO.findAll().stream()
                .filter(s -> s.getStatus() == SubmissionStatus.REJECTED)
                .count();
    }

    public Optional<Submission> getTopHypedSubmission() {
        return submissionDAO.findAll().stream()
                .max(Comparator.comparingInt(Submission::getHypeCount));
    }

    public String generateCsvReport() {
        StringBuilder csv = new StringBuilder();
        csv.append("Submission ID,Title,Type,Category,Location,Priority,Status,Hype Count,Created At\n");

        List<Submission> all = submissionDAO.findAll();
        for (Submission s : all) {
            csv.append(s.getSubmissionId()).append(",")
               .append("\"").append(s.getTitle().replace("\"", "\"\"")).append("\",")
               .append(s.getType().name()).append(",")
               .append(s.getCategory().name()).append(",")
               .append("\"").append(s.getLocation().replace("\"", "\"\"")).append("\",")
               .append(s.getPriority().name()).append(",")
               .append(s.getStatus().name()).append(",")
               .append(s.getHypeCount()).append(",")
               .append(s.getCreatedAt()).append("\n");
        }
        return csv.toString();
    }
}
