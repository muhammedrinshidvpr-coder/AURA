package aura.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Provides database connections from the local, git-ignored db.properties file. */
public final class DatabaseConfig {
    private static final Properties PROPERTIES = loadProperties();

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        String url = requiredProperty("db.url");
        String username = requiredProperty("db.user");
        String password = requiredProperty("db.password");
        String driver = PROPERTIES.getProperty("db.driver", "org.postgresql.Driver").trim();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException exception) {
            throw new SQLException("PostgreSQL JDBC driver is unavailable.", exception);
        }
        return DriverManager.getConnection(url, username, password);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load db.properties.", exception);
        }
        return properties;
    }

    private static String requiredProperty(String key) throws SQLException {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            String environmentKey = "AURA_" + key.substring("db.".length()).toUpperCase().replace('.', '_');
            value = System.getenv(environmentKey);
        }
        if (value == null || value.isBlank()) {
            throw new SQLException("Database is not configured: set " + key + " in db.properties or AURA_" + key.substring("db.".length()).toUpperCase() + ".");
        }
        return value.trim();
    }
}
