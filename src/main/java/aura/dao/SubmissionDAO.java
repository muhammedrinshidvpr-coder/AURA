package aura.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import aura.config.DatabaseConfig;
import aura.enums.Priority;
import aura.enums.SubmissionStatus;
import aura.enums.SubmissionType;
import aura.model.Submission;

/**
 * CRUD on {@code submissions}. Query methods are split by audience — see
 * docs/ARCHITECTURE.md section 5. {@link #findMySubmissions(int)} is the
 * only method in this class (or reachable from admin code) whose SELECT
 * includes {@code student_id}; every other query below never puts that
 * column in its SELECT list, so the anonymity boundary can't be broken by
 * a caller filtering the wrong field afterward — the data simply isn't there.
 */
public class SubmissionDAO {

    private static final String PUBLIC_COLUMNS =
        "submission_id, title, description, type, priority, status, created_at";

    public int insert(int studentId, String title, String description, SubmissionType type, Priority priority)
            throws SQLException {
        String sql = "INSERT INTO submissions (student_id, title, description, type, priority) "
            + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, type.name());
            ps.setString(5, priority.name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    /** The only query in the DAO layer permitted to select {@code student_id}. */
    public List<Submission> findMySubmissions(int studentId) throws SQLException {
        String sql = "SELECT submission_id, student_id, title, description, type, priority, status, created_at "
            + "FROM submissions WHERE student_id = ? ORDER BY created_at DESC, submission_id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Submission> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapRowWithStudentId(rs));
                }
                return results;
            }
        }
    }

    public List<Submission> findAllTrending() throws SQLException {
        String sql = "SELECT s.submission_id, s.title, s.description, s.type, s.priority, s.status, s.created_at "
            + "FROM submissions s ORDER BY "
            + "(SELECT COUNT(*) FROM submission_hype h WHERE h.submission_id = s.submission_id) DESC, "
            + "s.created_at DESC, s.submission_id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Submission> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapRowNoStudentId(rs));
            }
            return results;
        }
    }

    public List<Submission> findAllRecent() throws SQLException {
        String sql = "SELECT " + PUBLIC_COLUMNS + " FROM submissions ORDER BY created_at DESC, submission_id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Submission> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapRowNoStudentId(rs));
            }
            return results;
        }
    }

    public Optional<Submission> findPublicDetail(int submissionId) throws SQLException {
        String sql = "SELECT " + PUBLIC_COLUMNS + " FROM submissions WHERE submission_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRowNoStudentId(rs)) : Optional.empty();
            }
        }
    }

    public List<Submission> findAllForAdminQueue() throws SQLException {
        String sql = "SELECT " + PUBLIC_COLUMNS + " FROM submissions ORDER BY created_at DESC, submission_id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Submission> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapRowNoStudentId(rs));
            }
            return results;
        }
    }

    public Optional<Submission> findByIdForAdmin(int submissionId) throws SQLException {
        String sql = "SELECT " + PUBLIC_COLUMNS + " FROM submissions WHERE submission_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRowNoStudentId(rs)) : Optional.empty();
            }
        }
    }

    public boolean updateStatus(int submissionId, SubmissionStatus newStatus) throws SQLException {
        String sql = "UPDATE submissions SET status = ? WHERE submission_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, submissionId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Row mapper used by every admin/public-facing query — never reads student_id. */
    private Submission mapRowNoStudentId(ResultSet rs) throws SQLException {
        return new Submission(
            rs.getInt("submission_id"),
            null,
            rs.getString("title"),
            rs.getString("description"),
            SubmissionType.valueOf(rs.getString("type")),
            Priority.valueOf(rs.getString("priority")),
            SubmissionStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    /** Row mapper used only by {@link #findMySubmissions(int)}. */
    private Submission mapRowWithStudentId(ResultSet rs) throws SQLException {
        return new Submission(
            rs.getInt("submission_id"),
            rs.getInt("student_id"),
            rs.getString("title"),
            rs.getString("description"),
            SubmissionType.valueOf(rs.getString("type")),
            Priority.valueOf(rs.getString("priority")),
            SubmissionStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
