package aura.service;

import aura.config.OAuthConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Google OAuth 2.0 authorization-code flow for a native desktop application.
 * It uses a loopback receiver, PKCE, and a cryptographic state value.
 */
public class GoogleOAuthService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(20);
    private static final int CALLBACK_TIMEOUT_SECONDS = 120;
    private final HttpClient httpClient;

    public GoogleOAuthService() {
        this(HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build());
    }

    GoogleOAuthService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public record GoogleUserInfo(String email, String name) {
    }

    public GoogleUserInfo executeOAuthFlow() throws Exception {
        if (!OAuthConfig.isConfigured()) {
            throw new IllegalStateException("Google OAuth is not configured. Add valid credentials to oauth.properties.");
        }
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            throw new IllegalStateException("This desktop environment cannot open the system browser.");
        }

        String state = randomUrlToken(32);
        String verifier = randomUrlToken(48);
        String challenge = sha256UrlToken(verifier);
        CompletableFuture<String> authorizationCode = new CompletableFuture<>();
        HttpServer callbackServer = HttpServer.create(new InetSocketAddress("127.0.0.1", OAuthConfig.getServerPort()), 0);
        callbackServer.createContext("/callback", exchange -> handleCallback(exchange, state, authorizationCode));
        callbackServer.start();

        try {
            Desktop.getDesktop().browse(URI.create(buildAuthorizationUrl(state, challenge)));
            String code = authorizationCode.get(CALLBACK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            String token = exchangeCodeForToken(code, verifier);
            return fetchUserProfile(token);
        } finally {
            callbackServer.stop(0);
        }
    }

    private String buildAuthorizationUrl(String state, String challenge) {
        return "https://accounts.google.com/o/oauth2/v2/auth?"
                + "client_id=" + encode(OAuthConfig.getClientId())
                + "&redirect_uri=" + encode(OAuthConfig.getRedirectUri())
                + "&response_type=code"
                + "&scope=" + encode("openid email profile")
                + "&state=" + encode(state)
                + "&code_challenge=" + encode(challenge)
                + "&code_challenge_method=S256"
                + "&hd=" + encode(OAuthConfig.getEnforcedDomain());
    }

    private void handleCallback(HttpExchange exchange, String expectedState, CompletableFuture<String> authorizationCode) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "Only GET is accepted.");
            return;
        }
        Map<String, String> parameters = parseQuery(exchange.getRequestURI().getRawQuery());
        String error = parameters.get("error");
        String code = parameters.get("code");
        String state = parameters.get("state");

        if (error != null) {
            authorizationCode.completeExceptionally(new SecurityException("Google sign-in was cancelled or denied."));
            respond(exchange, 400, "Sign-in was cancelled. You may return to AURA.");
        } else if (code == null || !constantTimeEquals(expectedState, state)) {
            authorizationCode.completeExceptionally(new SecurityException("Invalid OAuth callback state."));
            respond(exchange, 400, "Authentication failed. You may close this tab.");
        } else {
            authorizationCode.complete(code);
            respond(exchange, 200, "Authentication succeeded. You may close this tab and return to AURA.");
        }
    }

    private String exchangeCodeForToken(String code, String verifier) throws Exception {
        String body = "code=" + encode(code)
                + "&client_id=" + encode(OAuthConfig.getClientId())
                + "&redirect_uri=" + encode(OAuthConfig.getRedirectUri())
                + "&grant_type=authorization_code"
                + "&code_verifier=" + encode(verifier);
        String secret = OAuthConfig.getClientSecret();
        if (!secret.isBlank() && !secret.startsWith("YOUR_")) body += "&client_secret=" + encode(secret);

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/token"))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new SecurityException("Google token exchange failed (HTTP " + response.statusCode() + ").");
        }
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.has("access_token")) throw new SecurityException("Google did not return an access token.");
        return json.get("access_token").getAsString();
    }

    private GoogleUserInfo fetchUserProfile(String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://openidconnect.googleapis.com/v1/userinfo"))
                .timeout(REQUEST_TIMEOUT)
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new SecurityException("Google user-info request failed (HTTP " + response.statusCode() + ").");
        }
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.has("email") || (json.has("email_verified") && !json.get("email_verified").getAsBoolean())) {
            throw new SecurityException("Google did not verify an email address for this account.");
        }
        String name = json.has("name") && !json.get("name").getAsString().isBlank()
                ? json.get("name").getAsString() : "TKMCE User";
        return new GoogleUserInfo(json.get("email").getAsString(), name);
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> parameters = new HashMap<>();
        if (query == null || query.isBlank()) return parameters;
        for (String pair : query.split("&")) {
            String[] pieces = pair.split("=", 2);
            String key = URLDecoder.decode(pieces[0], StandardCharsets.UTF_8);
            String value = pieces.length == 2 ? URLDecoder.decode(pieces[1], StandardCharsets.UTF_8) : "";
            parameters.put(key, value);
        }
        return parameters;
    }

    private static void respond(HttpExchange exchange, int status, String message) throws IOException {
        byte[] response = ("<html><body><h2>AURA</h2><p>" + message + "</p></body></html>").getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }

    private static String randomUrlToken(int bytes) {
        byte[] value = new byte[bytes];
        new SecureRandom().nextBytes(value);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private static String sha256UrlToken(String value) {
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.US_ASCII)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        if (actual == null) return false;
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
