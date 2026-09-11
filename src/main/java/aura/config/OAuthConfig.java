package aura.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Reads optional local OAuth configuration. Missing configuration keeps the app in dev mode. */
public final class OAuthConfig {
    private static final Properties PROPERTIES = loadProperties();

    private OAuthConfig() {
    }

    public static String getClientId() { return property("oauth.google.client.id", ""); }
    public static String getClientSecret() { return property("oauth.google.client.secret", ""); }
    public static String getRedirectUri() { return property("oauth.google.redirect.uri", "http://localhost:8080/callback"); }
    public static String getEnforcedDomain() { return property("oauth.enforced.domain", "tkmce.ac.in").toLowerCase(); }
    public static boolean isDevMode() { return Boolean.parseBoolean(property("oauth.dev.mode", "true")); }
    public static boolean isConfigured() { return !getClientId().isBlank() && !getClientId().startsWith("YOUR_"); }

    public static int getServerPort() {
        try {
            int port = Integer.parseInt(property("oauth.server.port", "8080"));
            if (port < 1 || port > 65535) throw new NumberFormatException("out of range");
            return port;
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("oauth.server.port must be a valid TCP port.", exception);
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = OAuthConfig.class.getClassLoader().getResourceAsStream("oauth.properties")) {
            if (input != null) properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load oauth.properties.", exception);
        }
        return properties;
    }

    private static String property(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue).trim();
    }
}
