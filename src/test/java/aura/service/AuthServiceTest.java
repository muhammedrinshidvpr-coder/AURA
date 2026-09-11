package aura.service;

import aura.util.SessionContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {
    @AfterEach
    void clearSession() {
        SessionContext.clearSession();
    }

    @Test
    void mockLoginSetsAStudentSessionInDevelopmentMode() {
        AuthService service = new AuthService(null, null);
        var user = service.loginMock("STUDENT");

        assertEquals("STUDENT", user.getRole());
        assertTrue(SessionContext.isAuthenticated());
        assertFalse(SessionContext.isAdmin());
    }

    @Test
    void adminMockSessionIsRecognizedAndLogoutClearsIt() {
        AuthService service = new AuthService(null, null);
        service.loginMock("ADMIN");

        assertTrue(service.isCurrentUserAdmin());
        service.logout();
        assertFalse(SessionContext.isAuthenticated());
    }

    @Test
    void mockLoginRejectsUnknownRoles() {
        AuthService service = new AuthService(null, null);
        assertThrows(IllegalArgumentException.class, () -> service.loginMock("SUPERADMIN"));
    }
}
