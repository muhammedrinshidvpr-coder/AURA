package aura.dao;

import aura.model.User;

/**
 * UserDAO — basic user persistence.
 * Assigned to: Rahandeep RD (B25CS053)
 */
public class UserDAO {

    public UserDAO() {}

    public void save(User user) {
        // TODO: INSERT INTO users (...) VALUES (...)
        throw new UnsupportedOperationException("save() not implemented yet");
    }

    public User findById(int id) {
        // TODO: SELECT * FROM users WHERE user_id = ?
        throw new UnsupportedOperationException("findById() not implemented yet");
    }

    public User findByEmail(String email) {
        // TODO: SELECT * FROM users WHERE email = ?
        throw new UnsupportedOperationException("findByEmail() not implemented yet");
    }

    public void update(User user) {
        // TODO: UPDATE users SET ... WHERE user_id = ?
        throw new UnsupportedOperationException("update() not implemented yet");
    }

    public void delete(int id) {
        // TODO: DELETE FROM users WHERE user_id = ?
        throw new UnsupportedOperationException("delete() not implemented yet");
    }
}
