# AGENT.md — Rules for anyone (human or AI) writing code in this repository

This is a **graded academic project**. Every class, every method, every SQL statement must be something a team member can personally explain to a teacher, line by line. That constraint outranks cleverness, speed, and "best practice for its own sake." If a rule below ever conflicts with looking impressive, the rule wins.

These rules apply to every future coding session in this repo, not just the first one. Read this file before writing or modifying any source file.

## 0. Docs are the source of truth

Before creating any class, its responsibility must already be described in [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md). If it isn't:

1. Stop.
2. Propose the addition to the relevant doc first (as a diff you can point to).
3. Only then write the class.

Do not improvise package structure, table columns, or service boundaries mid-coding. If a doc turns out to be wrong once you're implementing, fix the doc in the same change — don't let code and docs drift apart.

## 1. Layer boundaries are non-negotiable

```
aura.ui  →  aura.service  →  aura.dao  →  MySQL
```

- `aura.ui` classes may only call `aura.service` classes. Never `aura.dao` directly, never raw `java.sql.*`.
- `aura.service` classes may only call `aura.dao` classes for persistence. Never construct a `java.sql.Connection` themselves.
- `aura.dao` is the **only** package allowed to import `java.sql.*`.
- One `Connection` per DAO method call, opened and closed with try-with-resources. No hand-rolled connection pooling, no singleton connection held open for the app's lifetime — unnecessary at this scale and it removes your ability to explain what's happening on each call.

Full package map and class list: [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## 2. The anonymity boundary is a hard rule, not a suggestion

Posts are fully anonymous — including to admins. That guarantee is enforced entirely in code, so it can only be as strong as the discipline applied here:

- **No DAO or service method reachable from an admin-facing UI screen may ever `SELECT` or join `submissions.student_id`.**
- The only method allowed to touch that column is `SubmissionDAO.findMySubmissions(int studentId)`, called exclusively from the currently-logged-in student's own session — never from an admin code path, never with an arbitrary ID passed in from elsewhere.
- Any pull request or diff touching `SubmissionDAO`, `SubmissionService`, or anything under `aura.ui.admin` must be checked against this rule before it's considered done. Treat it like a security review gate, because it is one.
- There is a regression test for this (`AdminSubmissionQueryAnonymityTest` or equivalent — see [`docs/TESTING.md`](docs/TESTING.md)). It must exist and must pass before the feature is "done."

Full model and its honest limitations: [`docs/SECURITY.md`](docs/SECURITY.md).

## 3. No code without a matching test

Every class in `aura.dao` and `aura.service` gets a JUnit 5 test class written **in the same session** it's created, not deferred to "later." See [`docs/TESTING.md`](docs/TESTING.md) for what's required per class type.

## 4. `PreparedStatement` only

No string concatenation into SQL, ever — not even for values you're sure are "safe" (an admin-typed resolution note, a hardcoded status). This is a grep-checkable rule: a plain `Statement` object should not appear anywhere outside of schema-setup test helpers.

```java
// Wrong — never do this, even for internal/trusted input
String sql = "SELECT * FROM submissions WHERE status = '" + status + "'";

// Right
String sql = "SELECT * FROM submissions WHERE status = ?";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, status.name());
    ...
}
```

## 5. No plaintext passwords, ever

Passwords are hashed with BCrypt (`jbcrypt`) before they touch the database, and are never logged, printed, or included in exception messages.

## 6. Authorization is re-checked in the service layer, not just hidden in the UI

Hiding an admin button from a student in Swing is a UX nicety, not a security control. Every method in an admin-facing service must independently verify the caller's role from the current session before doing anything — assume the UI check didn't happen.

## 7. No speculative abstractions

Don't add interfaces, factory classes, config flags, or generic frameworks for hypothetical future needs. Three similar lines of code beat a premature abstraction — especially here, where every abstraction you add is something you must personally defend to a teacher. If you can't explain *why* an abstraction exists in one sentence, delete it and inline the code.

## 8. One class, one stated responsibility

Every class's purpose should match its one-line description in `docs/ARCHITECTURE.md`'s class list, and should be summarizable in a single plain sentence. If a method's purpose can't be explained that simply, it's doing too much — split it.

## 9. Definition of done

A feature/class is "done" only when **all** of the following are true:

- [ ] It compiles with no warnings from unused imports or dead code.
- [ ] It has a passing test (see rule 3).
- [ ] It matches the relevant section of `docs/ARCHITECTURE.md` / `docs/DATABASE.md` — or those docs were updated alongside it.
- [ ] If it touches submissions or admin queries, it passes the anonymity-boundary check (rule 2).
- [ ] A team member can explain every line of it out loud, unaided.

## 10. Build order

Don't jump ahead. Follow the milestone order in [`docs/ROADMAP.md`](docs/ROADMAP.md) — schema before models, models before DAOs, DAOs before services, services before UI. Each milestone must compile and be tested before the next one starts.

## Quick reference: other docs

- [`docs/PRD.md`](docs/PRD.md) — what & why
- [`docs/TRD.md`](docs/TRD.md) — tech stack, functional/non-functional requirements, explicit out-of-scope list
- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — layers, package map, class list
- [`docs/DATABASE.md`](docs/DATABASE.md) — ER diagram, schema, anonymity design
- [`docs/SECURITY.md`](docs/SECURITY.md) — auth, SQLi stance, anonymity limitations
- [`docs/TESTING.md`](docs/TESTING.md) — test requirements and conventions
- [`docs/ROADMAP.md`](docs/ROADMAP.md) — milestone build order
