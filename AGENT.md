# AGENT.md — Engineering Rules for AURA Coding Sessions

This is an **academic project at TKM College of Engineering (TKMCE)**. Every class, method, and SQL statement must be something a team member can personally defend to university examiners, line by line.

---

## 0. Docs are the Authoritative Source of Truth
Before creating or modifying any class, its responsibility must already be specified in [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) and [`docs/AURA.pdf`](docs/AURA.pdf). Do not improvise package names, table schemas, or service boundaries.

---

## 1. Layer Boundaries are Non-Negotiable
```
aura.ui (FlatLaf Swing)  →  aura.service  →  aura.dao  →  Supabase PostgreSQL
```
- `aura.ui` classes may **only** invoke `aura.service` classes. They must never touch `aura.dao` or import `java.sql.*`.
- `aura.service` classes orchestrate business rules, validate domain constraints, and invoke `aura.dao` classes. They must never construct a `Connection` directly.
- `aura.dao` is the **only** package permitted to import `java.sql.*`.
- Use try-with-resources for every `Connection`, `PreparedStatement`, and `ResultSet`.

---

## 2. Decoupled Anonymity Vault is an Absolute Law
- The `submissions` table has **no `student_id` column**.
- **No administrative code path** (`aura.ui.admin.*`, `AdminSubmissionService`) may ever attempt to access, query, or join `student_submission_receipts`.
- The only DAO permitted to access `student_submission_receipts` is `StudentReceiptDAO`, called exclusively by `TrackingService` for the authenticated student's own session.
- An automated regression test (`AdminAnonymityRegressionTest`) must pass before any pull request is merged.

---

## 3. Strict PreparedStatement Rule
No SQL string concatenation anywhere in the codebase:
```java
// WRONG: SQL Injection vulnerability
String sql = "SELECT * FROM submissions WHERE status = '" + status + "'";

// RIGHT: Parameterized PreparedStatement
String sql = "SELECT * FROM submissions WHERE status = ?";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, status.name());
    ...
}
```

---

## 4. Passwords Hashed with BCrypt
Passwords must be hashed using `jBCrypt` with work factor 10 before touching the database. Plaintext passwords must never be logged, printed, or included in exception messages.

---

## 5. UI Modernization with FlatLaf
- Entry point `aura.Main` must initialize the FlatLaf modern Look & Feel (`FlatDarkLaf` or `FlatLightLaf`) before any window is rendered.
- UI mockups from Slides 20–23 of [`docs/AURA.pdf`](docs/AURA.pdf) represent the design contract.

---

## 6. Definition of Done
A class or feature is "Done" only when:
- [ ] It compiles with zero warnings or unused imports.
- [ ] It has an automated JUnit 5 test class in `src/test/java/aura/`.
- [ ] It matches the approved UML diagram in [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).
- [ ] It strictly respects the Decoupled Anonymity Vault boundary.
- [ ] Any team member can explain every line out loud during an academic viva.
