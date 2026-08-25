package aura.support;

import java.sql.SQLException;

import aura.dao.UserDAO;
import aura.enums.Role;
import aura.model.User;
import aura.util.PasswordUtil;

/**
 * Test-only helper for creating a persisted student/admin user, used to set
 * up SessionContext in service-layer tests without repeating the same
 * insert-then-hydrate boilerplate in every test class.
 */
public final class TestUserSupport {

    private static final UserDAO userDAO = new UserDAO();

    private TestUserSupport() {
    }

    public static User createStudent(String email) throws SQLException {
        return create(email, Role.STUDENT);
    }

    public static User createAdmin(String email) throws SQLException {
        return create(email, Role.ADMIN);
    }

    private static User create(String email, Role role) throws SQLException {
        String hash = PasswordUtil.hash("password123");
        int userId = userDAO.insert(new User("Test User", email, hash, role));
        return new User(userId, "Test User", email, hash, role);
    }
}
