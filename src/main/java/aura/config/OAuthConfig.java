package aura.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Thread-safe configuration manager for Google OAuth 2.0.
 * Reads properties from oauth.properties with resilient fallbacks.
 */
public class OAuthConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = OAuthConfig.class.getClassLoader().getResourceAsStream("oauth.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                System.out.println("[INFO] oauth.properties not found on classpath. Using Dev Mock Mode defaults.");
            }
        } catch (IOException e) {
            System.err.println("[WARN] Failed to load oauth.properties: " + e.getMessage());
        }
    }

    public static String getClientId() {
        return props.getProperty("oauth.google.client.id", "").trim();
    }

    public static String getClientSecret() {
        return props.getProperty("oauth.google.client.secret", "").trim();
    }

    public static String getRedirectUri() {
        return props.getProperty("oauth.google.redirect.uri", "http://localhost:8080/callback").trim();
    }

    public static int getServerPort() {
        try {
            return Integer.parseInt(props.getProperty("oauth.server.port", "8080").trim());
        } catch (NumberFormatException e) {
            return 8080;
        }
    }

    public static String getEnforcedDomain() {
        return props.getProperty("oauth.enforced.domain", "tkmce.ac.in").trim();
    }

    public static boolean isDevMode() {
        return Boolean.parseBoolean(props.getProperty("oauth.dev.mode", "true").trim());
    }

    /**
     * Checks whether valid non-placeholder Google credentials are configured.
     */
    public static boolean isConfigured() {
        String cid = getClientId();
        String sec = getClientSecret();
        return !cid.isBlank() && !sec.isBlank() && !cid.contains("YOUR_GOOGLE_CLIENT_ID");
    }
}
