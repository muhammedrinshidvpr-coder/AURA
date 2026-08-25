package aura.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Reads db.properties from the classpath and hands out JDBC Connections.
 * One Connection per call — no pooling, no long-lived singleton connection
 * (see docs/AGENT.md rule 1).
 */
public final class DatabaseConfig {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new RuntimeException(
                    "db.properties not found on the classpath. Copy db.properties.example to "
                        + "db.properties (in the same resources folder) and fill in your local "
                        + "MySQL credentials.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read db.properties", e);
        }
        URL = props.getProperty("db.url");
        USER = props.getProperty("db.user");
        PASSWORD = props.getProperty("db.password");
    }

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
