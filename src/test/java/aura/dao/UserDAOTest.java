package aura.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aura.enums.Role;
import aura.model.User;
import aura.support.TestDatabaseSupport;

class UserDAOTest {

    private final UserDAO userDAO = new UserDAO();

    @BeforeEach
    void resetDatabase() throws SQLException {
        TestDatabaseSupport.resetTables();
    }

    @Test
    void insertThenFindByIdRoundTrips() throws SQLException {
        User user = new User("Asha Menon", "asha.menon@tkmce.ac.in", "hashed-password", Role.STUDENT);
        int id = userDAO.insert(user);

        Optional<User> found = userDAO.findById(id);

        assertTrue(found.isPresent());
        assertEquals("Asha Menon", found.get().getName());
        assertEquals("asha.menon@tkmce.ac.in", found.get().getEmail());
        assertEquals("hashed-password", found.get().getPasswordHash());
        assertEquals(Role.STUDENT, found.get().getRole());
    }

    @Test
    void findByEmailHitAndMiss() throws SQLException {
        userDAO.insert(new User("Divya Pillai", "divya.pillai@tkmce.ac.in", "hash", Role.ADMIN));

        Optional<User> hit = userDAO.findByEmail("divya.pillai@tkmce.ac.in");
        Optional<User> miss = userDAO.findByEmail("nobody@tkmce.ac.in");

        assertTrue(hit.isPresent());
        assertEquals(Role.ADMIN, hit.get().getRole());
        assertFalse(miss.isPresent());
    }

    @Test
    void duplicateEmailInsertThrowsSqlException() throws SQLException {
        userDAO.insert(new User("Rahul Nair", "rahul.nair@tkmce.ac.in", "hash1", Role.STUDENT));

        assertThrows(SQLException.class, () ->
            userDAO.insert(new User("Someone Else", "rahul.nair@tkmce.ac.in", "hash2", Role.STUDENT)));
    }
}
