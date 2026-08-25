package aura.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.enums.Role;
import aura.exception.InvalidCredentialsException;
import aura.model.User;
import aura.support.TestDatabaseSupport;
import aura.util.SessionContext;

class AuthServiceTest {

    private final AuthService authService = new AuthService();

    @BeforeEach
    void resetDatabase() throws SQLException {
        TestDatabaseSupport.resetTables();
        SessionContext.clear();
    }

    @AfterEach
    void clearSession() {
        SessionContext.clear();
    }

    @Test
    void registerRejectsNonTkmceEmail() {
        assertThrows(InvalidCredentialsException.class, () ->
            authService.register("Someone", "nonstudent@gmail.com", "password123", Role.STUDENT));
    }

    @Test
    void loginRejectsNonTkmceEmail() {
        assertThrows(InvalidCredentialsException.class, () ->
            authService.login("nonstudent@gmail.com", "password123"));
    }

    @Test
    void registerThenLoginRoundTripSucceeds() {
        authService.register("Asha Menon", "asha.menon@tkmce.ac.in", "password123", Role.STUDENT);
        SessionContext.clear();

        User loggedIn = authService.login("asha.menon@tkmce.ac.in", "password123");

        assertEquals("asha.menon@tkmce.ac.in", loggedIn.getEmail());
        assertEquals(Role.STUDENT, loggedIn.getRole());
    }

    @Test
    void loginWithWrongPasswordFails() {
        authService.register("Rahul Nair", "rahul.nair@tkmce.ac.in", "correct-password", Role.STUDENT);
        SessionContext.clear();

        assertThrows(InvalidCredentialsException.class, () ->
            authService.login("rahul.nair@tkmce.ac.in", "wrong-password"));
    }

    @Test
    void duplicateEmailRegistrationFailsCleanly() {
        authService.register("Divya Pillai", "divya.pillai@tkmce.ac.in", "password123", Role.STUDENT);

        assertThrows(InvalidCredentialsException.class, () ->
            authService.register("Divya Pillai Again", "divya.pillai@tkmce.ac.in", "password456", Role.STUDENT));
    }
}
