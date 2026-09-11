package aura.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Reads db.properties from the classpath and supplies JDBC Connections to the DAO layer.
 * Enforces single-use connections per operation using try-with-resources.
 */
public final class DatabaseConfig {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;
    private static final String DRIVER;

    static {
        // Cache DNS lookups for pooler host to prevent intermittent DNS resolution drops
        java.security.Security.setProperty("networkaddress.cache.ttl", "300");
        java.security.Security.setProperty("networkaddress.cache.negative.ttl", "0");

        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                    "db.properties not found on the classpath. Ensure src/main/resources/db.properties exists.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load db.properties: " + e.getMessage());
        }

        URL = props.getProperty("db.url");
        USER = props.getProperty("db.user");
        PASSWORD = props.getProperty("db.password");
        DRIVER = props.getProperty("db.driver", "org.postgresql.Driver");

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("PostgreSQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    private DatabaseConfig() {
        // Utility class
    }

    /**
     * Obtains a new live JDBC Connection to Supabase PostgreSQL with retry resilience.
     * Callers MUST close the connection using try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        SQLException lastEx = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                lastEx = e;
                try {
                    Thread.sleep(400);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Connection attempt interrupted: " + ie.getMessage(), ie);
                }
            }
        }
        throw lastEx;
    }
}
