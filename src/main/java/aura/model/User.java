package aura.model;

import aura.enums.Role;

public class User {

    private final Integer userId;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final Role role;

    /** Pre-insert: no id yet, assigned by the database on insert. */
    public User(String name, String email, String passwordHash, Role role) {
        this(null, name, email, passwordHash, role);
    }

    /** Hydration: reconstructing a row already in the database. */
    public User(Integer userId, String name, String email, String passwordHash, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }
}
