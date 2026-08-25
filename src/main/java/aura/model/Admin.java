package aura.model;

import aura.enums.Role;

/**
 * {@link User} specialization for role-based method dispatch and clarity —
 * carries no extra fields (see docs/ARCHITECTURE.md).
 */
public class Admin extends User {

    public Admin(String name, String email, String passwordHash) {
        super(name, email, passwordHash, Role.ADMIN);
    }

    public Admin(Integer userId, String name, String email, String passwordHash) {
        super(userId, name, email, passwordHash, Role.ADMIN);
    }
}
