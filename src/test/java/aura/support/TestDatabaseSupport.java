package aura.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import aura.config.DatabaseConfig;

/**
 * Test-only helper that empties every table in aura_test_db between tests,
 * in FK-safe (children-first) order, so each test starts from a known-clean
 * schema (see docs/TESTING.md section 4). Not part of the application —
 * only ever used from test classes' @BeforeEach.
 */
public final class TestDatabaseSupport {

    private static final String[] TABLES_CHILDREN_FIRST = {
        "resolution_notes", "submission_history", "submission_hype", "submissions", "users"
    };

    private TestDatabaseSupport() {
    }

    public static void resetTables() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String table : TABLES_CHILDREN_FIRST) {
                stmt.executeUpdate("DELETE FROM " + table);
            }
        }
    }
}
