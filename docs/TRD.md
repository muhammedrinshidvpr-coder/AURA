# Technical Requirements Document (TRD) — AURA

Companion to [`PRD.md`](PRD.md) (what & why) and [`ARCHITECTURE.md`](ARCHITECTURE.md) (how it's structured). This document is the authoritative technical scope — if a proposed feature or dependency isn't described here, it doesn't belong in the project yet without a doc update first (see [`AGENT.md`](../AGENT.md), rule 0).

## 1. Tech stack

| Concern | Choice | Why |
|---|---|---|
| Language | Java 17 (SE) | Course language; LTS release |
| GUI | Swing + Nimbus Look & Feel | No extra SDK/module-path setup (unlike JavaFX post-JDK 11), matches the approved mockups, easiest to explain to a grader |
| Build | Maven | Standard, dependency-managed, one `pom.xml` a teacher can open and understand |
| Database | MySQL 8.x | Matches the course's JDBC lab track; relational model fits the ER design |
| DB access | JDBC (`mysql-connector-j`), `PreparedStatement` only | Direct, explainable, no ORM magic to defend |
| Password hashing | jBCrypt | Small, single-purpose, well-known — a defensible answer to "why not roll your own hashing" |
| Testing | JUnit 5 | Standard, Maven-integrated |

No other frameworks. No Spring, no Lombok, no ORM (Hibernate/JPA), no REST API layer. This is a single desktop client talking directly to one MySQL database — anything beyond that adds parts of the system nobody on the team can defend line-by-line.

## 2. Functional requirements

Derived from the original use-case diagram, extended for the anonymous + hype model:

| ID | Requirement | Actor |
|---|---|---|
| FR-1 | Register / log in with a TKMCE-verified identity | Student, Admin |
| FR-2 | Submit an Issue or Suggestion (type, priority, title, description) — anonymously | Student |
| FR-3 | View a list of submissions, sortable by **Trending** (hype count) or **Recent** | Student |
| FR-4 | Hype a submission (one hype per student per submission); un-hype to remove it | Student |
| FR-5 | View **My Submissions** — own reports and current status, regardless of anonymity to others | Student |
| FR-6 | View resolution notes on own resolved submissions | Student |
| FR-7 | View all submissions queue, sortable by priority/hype/date — without author identity | Admin |
| FR-8 | Assign a submission and update its status (`PENDING → ASSIGNED → IN_PROGRESS → RESOLVED / REJECTED`) | Admin |
| FR-9 | Add a resolution note when resolving/rejecting | Admin |
| FR-10 | View dashboard statistics (counts per status) | Admin |
| FR-11 | Export a CSV report of submissions/resolutions | Admin |
| FR-12 | Log out | Student, Admin |

FR-5 / FR-6 are carried over unchanged from the original design and are called out explicitly here because they're the one place the anonymity requirement (FR-2 + SECURITY.md) has to coexist with a feature that looks, at first glance, like it needs identity: a student must always be able to find their own posts even though no one else — including admin — can.

## 3. Non-functional requirements

- **NFR-1 — No plaintext passwords.** BCrypt hash only, in the database and in memory beyond the point of hashing.
- **NFR-2 — SQL injection is not possible by construction.** Every query uses `PreparedStatement`; no SQL string concatenation anywhere in the codebase, including for values assumed to be trusted.
- **NFR-3 — One hype per student per submission**, enforced at the database level (`UNIQUE` constraint), not just in application logic.
- **NFR-4 — Anonymity guarantee**, enforced at the service layer: no admin-reachable code path may select or join `submissions.student_id`. Documented honestly as an application-layer guarantee, not cryptographic — see [`SECURITY.md`](SECURITY.md) for the exact boundary and its limits.
- **NFR-5 — Institutional access only.** Registration/login rejects any email that doesn't match the TKMCE institutional domain (`@tkmce.ac.in`), checked in one place, not duplicated across screens.
- **NFR-6 — Performance is intentionally unambitious.** At campus scale (hundreds, not millions, of submissions), a `COUNT()` query for hype totals and straightforward indexed lookups are more than sufficient. This is stated explicitly so no one adds caching, connection pooling libraries, or denormalized counters "for scale" that don't exist here — see out-of-scope list below.
- **NFR-7 — Every input has a server-side length/format check** in the service layer, not just a DB column limit, so bad input fails with a clear message instead of a raw SQL error reaching the UI.

## 4. Explicitly out of scope (technical)

To keep the project buildable, testable, and defensible within the real timeline in [`ROADMAP.md`](ROADMAP.md):

- No REST API / web server — Swing talks directly to the service layer, which talks directly to the DAO layer.
- No connection pooling library (HikariCP etc.) — one `Connection` per DAO call via try-with-resources is sufficient and far easier to explain.
- No caching layer.
- No ORM / JPA / Hibernate — plain JDBC in the DAO layer only.
- No microservices, no multi-module Maven build — this is one application, one `pom.xml`.
- No cryptographic/unlinkable anonymity (e.g. a client-held tracking-token scheme with zero server-side identity linkage) — noted as a documented stretch goal in [`SECURITY.md`](SECURITY.md), not built for v1.

## 5. Course concept coverage

TKMCE's S3 "Advanced Programming" course maps to KTU's **CST205 (Object Oriented Programming Using Java)** + **CSL203 (OOP Lab in Java)**. The published CSL203 lab cycle explicitly covers JDBC, Swing event handling, exception handling, collections, file handling, and inheritance/polymorphism — which is exactly this project's stack. The table below is the team's ready answer to "why did you build it this way, and what concept does it demonstrate":

| Course concept | Where it lives in AURA |
|---|---|
| Inheritance / polymorphism | `User → Student, Admin`; shared `Submission` behavior across `Issue`/`Suggestion` |
| Exception handling | `aura.exception` hierarchy (`AuraException`, `InvalidCredentialsException`, `UnauthorizedActionException`, ...) instead of raw `SQLException` reaching the UI |
| Collections | `List<Submission>`, `Map<String,Object>` dashboard statistics in service return types |
| File handling | `ReportService` CSV export |
| JDBC | The entire `aura.dao` package — `Connection`, `PreparedStatement`, `ResultSet` |
| GUI event handling | Swing listeners throughout `aura.ui` |
| Enums | `Role`, `SubmissionType`, `Priority`, `SubmissionStatus` |

## 6. Login/identity requirement detail

- Domain: `@tkmce.ac.in` (verified against the college's official domain).
- Enforced in exactly one place — `aura.util.ValidationUtil` (or equivalent) — and called from both the Swing login/registration form (fast feedback) and `AuthService` (the authoritative check; the UI check is a convenience, not a security boundary — see [`AGENT.md`](../AGENT.md) rule 6).
- If TKMCE later turns out to issue a different pattern for student accounts (e.g. a roll-number-based portal ID rather than a free-form `@tkmce.ac.in` address), only that one validation method needs to change.
