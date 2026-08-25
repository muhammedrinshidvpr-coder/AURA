# Testing — AURA

Companion to [`AGENT.md`](../AGENT.md) rule 3 ("no code without a matching test"). This document defines what "matching test" means per class type, so it's not left to interpretation mid-coding session.

## 1. Philosophy

- Tests are written **in the same session** the class they cover is written — not "at the end," which is exactly the mistake the original PPT roadmap made (tests deferred to the final week). A class without a test isn't done, per [`AGENT.md`](../AGENT.md) rule 9.
- Tests exist to let the team **prove** claims made in [`SECURITY.md`](SECURITY.md) and [`ARCHITECTURE.md`](ARCHITECTURE.md) — especially the anonymity boundary — not just to hit a coverage number.
- JUnit 5, run via `mvn test`. No separate test framework, no mocking library beyond what JUnit provides — a DAO test can run against a real disposable test schema (see §4), which is more convincing to a grader than a mocked `Connection`.

## 2. What must be tested, per layer

### `aura.dao`
- One test class per DAO (`UserDAOTest`, `SubmissionDAOTest`, `SubmissionHypeDAOTest`, ...).
- Cover: insert, find-by-id, update, and — critically — that admin-facing query methods (e.g. `SubmissionDAO.findAllForAdminQueue()`) **do not return a populated `studentId`** on the returned objects. This is a direct, automated check of the anonymity boundary, not just a code-review promise.
- Cover the `submission_hype` `UNIQUE(submission_id, student_id)` constraint: inserting a duplicate hype must fail predictably (caught and surfaced as `DuplicateHypeException` by the service layer — tested there too, see below).

### `aura.service`
- One test class per service (`AuthServiceTest`, `SubmissionServiceTest`, `HypeServiceTest`, `AdminSubmissionServiceTest`, ...).
- `AuthServiceTest`: TKMCE-domain rejection (`nonstudent@gmail.com` must fail), password hash round-trip (register then log in succeeds; wrong password fails), duplicate email registration fails cleanly.
- `SubmissionServiceTest`: creating a submission defaults to `PENDING`; trending sort actually orders by hype count; recent sort actually orders by `created_at`.
- `HypeServiceTest`: first hype from a student succeeds; second hype from the same student on the same submission throws `DuplicateHypeException`; un-hyping and re-hyping is allowed.
- `AdminSubmissionServiceTest`: status transitions follow the allowed pipeline (`PENDING → ASSIGNED → IN_PROGRESS → RESOLVED/REJECTED`); calling any admin method with a `STUDENT` session throws `UnauthorizedActionException`.
- **`AdminAnonymityBoundaryTest`** (or folded into `AdminSubmissionServiceTest`): explicitly asserts that every object returned by an admin-facing service method has no accessible/populated student identity — this is the regression test referenced in [`AGENT.md`](../AGENT.md) rule 2 and [`SECURITY.md`](SECURITY.md) §5. It must exist before the anonymity feature is considered done.

### `aura.util`
- `PasswordUtilTest`: a hashed password verifies correctly against the original plaintext and fails against a wrong one.
- `ValidationUtilTest`: TKMCE domain check accepts valid addresses and rejects everything else (including near-misses like `@tkmce.ac.in.evil.com`); length/format checks on submission title/description.

### `aura.ui`
Not unit tested in the traditional sense (Swing UI testing is disproportionate effort for a course project). Instead: a manual golden-path walkthrough (see [`PRD.md`](PRD.md) §6 success criteria) is run and confirmed before a milestone in [`ROADMAP.md`](ROADMAP.md) is called done.

## 3. Naming convention

`ClassUnderTestTest` (e.g. `SubmissionDAO` → `SubmissionDAOTest`), placed under `src/test/java/aura/...` mirroring the `src/main/java/aura/...` package structure exactly.

## 4. Test database

DAO and service tests that touch the database run against a disposable local schema (e.g. `aura_test_db`, created from the same [`../sql/schema.sql`](../sql/schema.sql) used for the real database), reset between test runs. This keeps DAO tests honest — they exercise real `PreparedStatement`/`ResultSet` code, not a mock standing in for it — while never touching real data.

## 5. Definition of "tested," per [`AGENT.md`](../AGENT.md) rule 9

A class is not "done" until:
- [ ] Its test class exists and passes (`mvn test` is green).
- [ ] If it's a DAO or service touching `submissions`, the anonymity-boundary assertion applies and passes.
- [ ] If it's `HypeService` or `SubmissionHypeDAO`, the duplicate-hype rejection is tested.
- [ ] If it's `AuthService` or `ValidationUtil`, the TKMCE-domain rejection is tested.
