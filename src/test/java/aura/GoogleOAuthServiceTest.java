package aura;

import aura.enums.Role;
import aura.model.User;
import aura.service.AuthService;
import aura.service.GoogleOAuthService;
import aura.util.SessionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite for Google OAuth 2.0 Desktop Authentication,
 * domain validation gating (@tkmce.ac.in), and auto-provisioning rules.
 */
public class GoogleOAuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
        SessionContext.clear();
    }

    @Test
    @DisplayName("Google OAuth rejects external domains like @gmail.com or @yahoo.com")
    void testDomainEnforcementRejectsExternalDomains() {
        GoogleOAuthService.GoogleUserInfo outsider =
                new GoogleOAuthService.GoogleUserInfo("student@gmail.com", "Outsider", null);

        assertThrows(SecurityException.class, () -> authService.loginWithGoogle(outsider),
                "Should throw SecurityException for non-@tkmce.ac.in accounts");
    }

    @Test
    @DisplayName("Google OAuth rejects null or empty email profiles")
    void testRejectsNullOrBlankEmails() {
        GoogleOAuthService.GoogleUserInfo nullEmail =
                new GoogleOAuthService.GoogleUserInfo(null, "No Email", null);
        assertThrows(IllegalArgumentException.class, () -> authService.loginWithGoogle(nullEmail));

        GoogleOAuthService.GoogleUserInfo emptyEmail =
                new GoogleOAuthService.GoogleUserInfo("  ", "Blank Email", null);
        assertThrows(IllegalArgumentException.class, () -> authService.loginWithGoogle(emptyEmail));
    }

    @Test
    @DisplayName("Google OAuth preserves pre-seeded ADMIN privileges for institutional administrators")
    void testExistingAdminRetainsAdminRole() {
        org.junit.jupiter.api.Assumptions.assumeTrue(aura.config.DatabaseConfig.isConfigured(),
                "Skipping live DB integration test: Supabase credentials not configured in this environment");

        GoogleOAuthService.GoogleUserInfo adminInfo =
                new GoogleOAuthService.GoogleUserInfo("admin@tkmce.ac.in", "Campus Maintenance Admin", null);

        User authenticated = authService.loginWithGoogle(adminInfo);
        assertNotNull(authenticated);
        assertEquals("admin@tkmce.ac.in", authenticated.getEmail().toLowerCase());
        assertEquals(Role.ADMIN, authenticated.getRole(), "Existing admin account must retain Role.ADMIN");
        assertEquals(authenticated, SessionContext.getCurrentUser());
    }

    @Test
    @DisplayName("Google OAuth auto-provisions newly authenticated students with Role.STUDENT (Least Privilege)")
    void testAutoProvisioningNewStudent() {
        org.junit.jupiter.api.Assumptions.assumeTrue(aura.config.DatabaseConfig.isConfigured(),
                "Skipping live DB integration test: Supabase credentials not configured in this environment");

        String uniqueStudentEmail = "auto.student." + System.currentTimeMillis() + "@tkmce.ac.in";
        String studentName = "Test Auto-Provisioned Student";

        GoogleOAuthService.GoogleUserInfo newStudent =
                new GoogleOAuthService.GoogleUserInfo(uniqueStudentEmail, studentName, null);

        User provisioned = authService.loginWithGoogle(newStudent);
        assertNotNull(provisioned);
        assertTrue(provisioned.getUserId() > 0, "Provisioned student must have generated user_id");
        assertEquals(uniqueStudentEmail.toLowerCase(), provisioned.getEmail().toLowerCase());
        assertEquals(studentName, provisioned.getName());
        assertEquals(Role.STUDENT, provisioned.getRole(), "New users must strictly be assigned Role.STUDENT");
        assertEquals("OAUTH_GOOGLE", provisioned.getPasswordHash());
        assertEquals(provisioned, SessionContext.getCurrentUser());

        // Cleanup temporary test user from database
        try (java.sql.Connection conn = aura.config.DatabaseConfig.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            ps.setInt(1, provisioned.getUserId());
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }
}
