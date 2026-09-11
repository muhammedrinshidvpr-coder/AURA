package aura.service;

import aura.config.OAuthConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implements RFC 8252 (OAuth 2.0 for Native Apps) via an embedded loopback HTTP server.
 * Handles system browser authorization, code exchange, and Google UserInfo extraction.
 */
public class GoogleOAuthService {

    public record GoogleUserInfo(String email, String name, String pictureUrl) {}

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final AtomicReference<HttpServer> activeServer = new AtomicReference<>(null);
    private final AtomicReference<CompletableFuture<String>> activeFuture = new AtomicReference<>(null);

    /**
     * Executes the RFC 8252 OAuth flow.
     * If real credentials are missing and dev mode is enabled, returns a default mock student info.
     */
    public GoogleUserInfo executeOAuthFlow() throws Exception {
        if (!OAuthConfig.isConfigured()) {
            if (OAuthConfig.isDevMode()) {
                System.out.println("[INFO] Google OAuth credentials not configured in oauth.properties. Using Dev Mock Mode.");
                return new GoogleUserInfo("student1@tkmce.ac.in", "Muhammed Rinshid VP", null);
            } else {
                throw new IllegalStateException("Google OAuth credentials are missing in oauth.properties!");
            }
        }

        String clientId = OAuthConfig.getClientId();
        String clientSecret = OAuthConfig.getClientSecret();
        String redirectUri = OAuthConfig.getRedirectUri();
        int port = OAuthConfig.getServerPort();

        // 1. Generate CSRF state token
        byte[] randomBytes = new byte[16];
        new SecureRandom().nextBytes(randomBytes);
        String stateToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        CompletableFuture<String> authCodeFuture = new CompletableFuture<>();
        activeFuture.set(authCodeFuture);

        // 2. Start loopback HTTP server on 127.0.0.1
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        activeServer.set(server);

        server.createContext("/callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String code = null;
            String returnedState = null;

            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        String key = pair[0];
                        String val = pair[1];
                        if ("code".equals(key)) code = val;
                        if ("state".equals(key)) returnedState = val;
                    }
                }
            }

            String htmlResponse;
            int statusCode;

            if (code != null && stateToken.equals(returnedState)) {
                authCodeFuture.complete(code);
                statusCode = 200;
                htmlResponse = """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <title>AURA — Authentication Successful</title>
                        <style>
                            body {
                                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                                background: #0b0f17;
                                color: #f3f4f6;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                height: 100vh;
                                margin: 0;
                            }
                            .card {
                                background: #111827;
                                border: 1px solid #374151;
                                padding: 40px;
                                border-radius: 12px;
                                text-align: center;
                                max-width: 440px;
                                box-shadow: 0 20px 40px rgba(0,0,0,0.6);
                            }
                            .badge {
                                display: inline-block;
                                background: rgba(16, 185, 129, 0.15);
                                color: #10b981;
                                border: 1px solid #10b981;
                                padding: 6px 14px;
                                border-radius: 20px;
                                font-size: 13px;
                                font-weight: 600;
                                margin-bottom: 16px;
                            }
                            h2 { margin: 0 0 12px 0; font-size: 22px; color: #f9fafb; }
                            p { color: #9ca3af; font-size: 14px; line-height: 1.5; margin: 0; }
                        </style>
                    </head>
                    <body>
                        <div class="card">
                            <div class="badge">✓ TKMCE Google Verified</div>
                            <h2>Authentication Successful!</h2>
                            <p>Your institutional Google Workspace session has been validated. You may close this browser tab and return to <strong>AURA</strong>.</p>
                        </div>
                    </body>
                    </html>
                    """;
            } else {
                authCodeFuture.completeExceptionally(new SecurityException("Invalid OAuth callback or state mismatch."));
                statusCode = 400;
                htmlResponse = """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: sans-serif; background: #0b0f17; color: #ef4444; text-align: center; padding-top: 50px;">
                        <h2>Authentication Failed</h2>
                        <p>State mismatch or invalid authorization code. Please return to AURA and try again.</p>
                    </body>
                    </html>
                    """;
            }

            byte[] bytes = htmlResponse.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        server.start();

        try {
            // 3. Construct Google OAuth Authorization URL
            String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?"
                    + "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                    + "&response_type=code"
                    + "&scope=" + URLEncoder.encode("openid email profile", StandardCharsets.UTF_8)
                    + "&state=" + URLEncoder.encode(stateToken, StandardCharsets.UTF_8)
                    + "&hd=" + URLEncoder.encode(OAuthConfig.getEnforcedDomain(), StandardCharsets.UTF_8);

            // 4. Launch System Default Browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(authUrl));
            } else {
                throw new UnsupportedOperationException("Desktop browser interaction is not supported on this environment.");
            }

            // 5. Await redirect callback (up to 120 seconds)
            String authCode = authCodeFuture.get(120, TimeUnit.SECONDS);

            // 6. Exchange Authorization Code for Access Token
            String accessToken = exchangeCodeForToken(authCode, clientId, clientSecret, redirectUri);

            // 7. Retrieve Google User Profile Details
            return fetchUserProfile(accessToken);

        } finally {
            cancelOAuthFlow();
        }
    }

    /**
     * Cancels any in-flight loopback server and waiting future.
     */
    public void cancelOAuthFlow() {
        HttpServer server = activeServer.getAndSet(null);
        if (server != null) {
            server.stop(0);
        }
        CompletableFuture<String> future = activeFuture.getAndSet(null);
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
    }

    private String exchangeCodeForToken(String code, String clientId, String clientSecret, String redirectUri) throws Exception {
        String requestBody = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to exchange code with Google: HTTP " + response.statusCode() + " — " + response.body());
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.has("access_token")) {
            throw new IOException("Google token response did not contain access_token.");
        }
        return json.get("access_token").getAsString();
    }

    private GoogleUserInfo fetchUserProfile(String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.googleapis.com/oauth2/v3/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch user profile from Google: HTTP " + response.statusCode());
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        String email = json.get("email").getAsString();
        String name = json.has("name") ? json.get("name").getAsString() : "TKMCE Member";
        String picture = json.has("picture") ? json.get("picture").getAsString() : null;

        return new GoogleUserInfo(email, name, picture);
    }
}
