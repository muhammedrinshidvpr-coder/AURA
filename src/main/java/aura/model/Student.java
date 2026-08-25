package aura.model;

import aura.enums.Role;

/**
 * {@link User} specialization for role-based method dispatch and clarity —
 * carries no extra fields (see docs/ARCHITECTURE.md).
 */
public class Student extends User {

    public Student(String name, String email, String passwordHash) {
        super(name, email, passwordHash, Role.STUDENT);
    }

    public Student(Integer userId, String name, String email, String passwordHash) {
        super(userId, name, email, passwordHash, Role.STUDENT);
    }
}
