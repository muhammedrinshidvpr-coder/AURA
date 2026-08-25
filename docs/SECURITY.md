# Security — AURA

Companion to [`DATABASE.md`](DATABASE.md) §3 (anonymity design) and [`ARCHITECTURE.md`](ARCHITECTURE.md) §5 (anonymity boundary). This document exists so the team has a written, defensible answer for every security question a teacher is likely to ask.

## 1. Institutional identity gate

Only TKMCE identities may use the platform: registration and login both reject any email that doesn't match the college's domain, `@tkmce.ac.in`.

- Checked in exactly one place, `aura.util.ValidationUtil`, called from both the Swing form (fast feedback) and `AuthService.validateCredentials()` (the check that actually matters — see [`AGENT.md`](../AGENT.md) rule 6: never trust a UI-only check).
- This is a scoping control, not a strong authentication mechanism on its own — it doesn't prove someone is a real TKMCE student, only that they used a `tkmce.ac.in` address. Combined with password auth, that's an appropriate bar for a closed campus tool; it is explicitly **not** claimed to be SSO-grade identity verification.

## 2. Password storage

- Passwords are hashed with **BCrypt** (`jbcrypt`) before ever reaching the database. `password_hash` stores only the BCrypt output (60 characters).
- Passwords are never logged, printed to console, or included in exception messages — including during debugging. If a bug needs a password value to reproduce, use a throwaway test account, not a real credential in a log line.
- `aura.util.PasswordUtil` is the only class that calls jBCrypt directly — hashing/verification logic isn't duplicated across the codebase.

## 3. SQL injection

- Every query, in every DAO class, uses `PreparedStatement` with bound parameters. No SQL string is ever built by concatenating user input — or any input, including values the code "knows" are safe (an enum's `.name()`, a hardcoded status). See the example in [`AGENT.md`](../AGENT.md) rule 4.
- This is enforced by convention and code review (grep for `Statement` outside `dao` test helpers, grep for `+ "` near anything with `sql` in the name), since a course-scale project doesn't warrant an ORM or a static-analysis pipeline to catch this automatically.

## 4. Authorization

- Every method in an admin-facing service (`AdminSubmissionService`, `ReportService`) independently re-validates that the caller's session role is `ADMIN` before doing anything — it does not trust that the UI only shows admin screens to admins.
- Similarly, student-only actions (submitting, hyping, viewing "My Submissions") re-check that a session exists and belongs to a `STUDENT`.
- There is no separate "permissions table" or role-hierarchy system — with exactly two roles and a small, fixed set of actions per role, a permissions engine would be exactly the kind of speculative abstraction [`AGENT.md`](../AGENT.md) rule 7 warns against. A simple `if (session.getRole() != Role.ADMIN) throw new UnauthorizedActionException(...)` at the top of each admin service method is correct, sufficient, and easy to point to.

## 5. Anonymity model

Full schema-level reasoning is in [`DATABASE.md`](DATABASE.md) §3; this section is the security framing of the same design.

**Guarantee:** no admin-facing screen, service method, DAO query, or exported report ever reveals which student authored a submission.

**Mechanism:** `submissions.student_id` remains a real FK in the schema (needed for a student to retrieve their own history), but it is treated as a **service-layer secret** — no code path reachable from `aura.ui.admin.*` is permitted to select or join it. This is enforced by:
1. The DAO methods used by admin services simply don't include that column in their `SELECT` list.
2. A regression test (see [`TESTING.md`](TESTING.md)) that asserts the admin-facing query methods' result objects never carry a populated `studentId`.
3. Code review discipline stated as a hard rule in [`AGENT.md`](../AGENT.md) rule 2.

**What this guarantee does *not* cover, stated honestly:**
- Anyone with **direct SQL access** to the production database — not through the application — could still run a raw query joining `submissions.student_id` to `users` and de-anonymize every post. This is an application-layer guarantee, not a cryptographic one.
- This is disclosed here rather than glossed over, because an academic project should be judged on accurately describing what it does and doesn't guarantee, not on overselling a feature.
- **Stretch goal, not built for v1:** a client-held tracking-token scheme where the server never stores a submission↔student link at all, removing even the direct-DB-access risk. Documented in [`DATABASE.md`](DATABASE.md) §3 as future work.

**Hype voting is a separate, non-anonymous identity relationship** — `submission_hype.student_id` records who voted, which is necessary to enforce one-hype-per-student, and does not weaken the poster-anonymity guarantee above (knowing who voted on a submission tells you nothing about who posted it).

## 6. Input validation

- Every user-supplied field (submission title, description, name) has a server-side length/format check in the relevant service class, not just a DB column-length limit — so invalid input produces a clear application error instead of a raw SQL/driver exception surfacing in the UI.
- This is defense in depth applied to correctness, mirroring the same principle applied to authorization in §4: never rely on only one layer to catch a problem.

## 7. Hype abuse control

- One hype per student per submission is enforced **twice**: a `UNIQUE(submission_id, student_id)` constraint at the database level (the authoritative guarantee — see [`DATABASE.md`](DATABASE.md) §2), and a service-level check in `HypeService` that turns the resulting DB constraint violation into a clear `DuplicateHypeException` instead of a raw SQL error reaching the UI.
- No self-hype prevention is needed or implemented: since submissions are posted anonymously, there is no comparison possible (or meaningful) between "who posted this" and "who is hyping this" — the system has no way to know they're the same person, by design.

## 8. What this project deliberately does not attempt

Stated so it reads as a scoping decision, not an oversight:

- No protection against a malicious database administrator (out of scope for an application-layer security model; see §5).
- No rate limiting / anti-spam beyond the one-hype-per-student constraint — not needed at a single-campus, authenticated-user scale.
- No encryption of data at rest — MySQL's standard access controls plus TKMCE-only login are the stated security perimeter for this version.
- No CSRF/XSS concerns in the traditional sense — this is a desktop Swing client, not a web app; the equivalent risk (untrusted input rendered back to another user, e.g. in a submission description shown on someone else's screen) is handled by the input-validation rule in §6, not by web-specific mitigations that don't apply here.
