# Architecture — AURA

Companion to [`TRD.md`](TRD.md) (requirements) and [`DATABASE.md`](DATABASE.md) (schema). This is the map every class must appear on before it's written — see [`AGENT.md`](../AGENT.md) rule 0.

## 1. Layered design

```
┌──────────────────────────────────────────────┐
│  aura.ui            Swing screens & listeners │
└───────────────────────┬────────────────────────┘
                         │ calls
┌───────────────────────▼────────────────────────┐
│  aura.service       Validation, business rules │
│                     session/role checks         │
└───────────────────────┬────────────────────────┘
                         │ calls
┌───────────────────────▼────────────────────────┐
│  aura.dao           SQL, PreparedStatement,     │
│                     ResultSet → object mapping   │
└───────────────────────┬────────────────────────┘
                         │ JDBC
┌───────────────────────▼────────────────────────┐
│  MySQL              users, submissions,         │
│                     submission_hype,             │
│                     submission_history,          │
│                     resolution_notes             │
└──────────────────────────────────────────────┘
```

**Hard rule:** calls only ever go downward (UI → Service → DAO → DB). A layer never reaches back up, and no layer skips the one directly beneath it. `aura.dao` is the only package permitted to import `java.sql.*`.

## 2. Package map

```
aura
├── Main.java                 entry point: sets Nimbus L&F, opens LoginFrame
├── config
│   └── DatabaseConfig.java   reads db.properties, hands out Connections
├── model                     plain data classes (no logic beyond simple getters/behavior)
├── enums
├── dao                       JDBC only, one class per table/aggregate
├── service                   business rules, session/role checks, orchestrates DAOs
├── ui                        Swing frames/panels, split into ui.student / ui.admin
├── util                      small stateless helpers
└── exception                 checked/unchecked exception hierarchy
```

## 3. Class list per package

### `aura.model`
| Class | Responsibility |
|---|---|
| `User` | Shared identity fields (id, name, email, password hash, role). Base for `Student`/`Admin`. |
| `Student` | `User` specialization — no extra fields; exists for role-based method dispatch and clarity. |
| `Admin` | `User` specialization — same reasoning. |
| `Submission` | One reported issue/suggestion: type, priority, title, description, status, timestamps. **Never carries `studentId` outside of the narrow ownership path — see §5.** |
| `SubmissionHistory` | One status transition record (old status → new status, who changed it, when). |
| `ResolutionNote` | One admin note attached to a submission at resolution time. |
| `SubmissionHype` | One hype record: which submission, which voting student, when. |

### `aura.enums`
| Enum | Values |
|---|---|
| `Role` | `STUDENT`, `ADMIN` |
| `SubmissionType` | `ISSUE`, `SUGGESTION` |
| `Priority` | `LOW`, `MEDIUM`, `HIGH` |
| `SubmissionStatus` | `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `REJECTED` |

### `aura.dao`
| Class | Responsibility |
|---|---|
| `UserDAO` | CRUD on `users`; lookup by email for login. |
| `SubmissionDAO` | CRUD on `submissions`. Public query methods intentionally split by audience — see the anonymity boundary in §5. |
| `SubmissionHistoryDAO` | Insert/read status-change audit rows. |
| `ResolutionNoteDAO` | Insert/read resolution notes for a submission. |
| `SubmissionHypeDAO` | Insert a hype (respecting the DB `UNIQUE` constraint), delete a hype, count hypes per submission. |

### `aura.service`
| Class | Responsibility |
|---|---|
| `AuthService` | Register/login: TKMCE-domain validation, password hashing/verification, session creation. |
| `SubmissionService` | Create a submission, fetch trending/recent lists (no author identity), fetch a single submission's public detail. |
| `TrackingService` | `findMySubmissions()` for the logged-in student only; status lookups for a student's own history. |
| `HypeService` | Add/remove a hype for the current student on a submission; enforce one-per-student at the service layer as a second line of defense behind the DB constraint. |
| `AdminSubmissionService` | Admin-only: queue view (no author identity), assign, update status, attach resolution note. Re-validates admin role on every call (see [`AGENT.md`](../AGENT.md) rule 6). |
| `ReportService` | Builds the CSV export for admin. |

### `aura.ui`
Split into `aura.ui.student` and `aura.ui.admin` so the anonymity/authorization boundary in §5 is visible in the folder structure itself, not just in a rule someone has to remember.

| Class | Responsibility |
|---|---|
| `LoginFrame` | TKMCE-domain-gated login/registration screen. |
| `ui.student.StudentDashboardFrame` | Trending/Recent list + My Submissions tab. |
| `ui.student.SubmissionFormPanel` | Create-submission form. |
| `ui.admin.AdminDashboardFrame` | Queue, assignment, resolution note entry, dashboard stats. |
| `ui.admin.ReportPanel` | Triggers CSV export via `ReportService`. |

### `aura.util`
| Class | Responsibility |
|---|---|
| `PasswordUtil` | Wraps jBCrypt hash/verify calls — the only place BCrypt is called directly. |
| `ValidationUtil` | TKMCE email-domain check, submission title/description length checks — the single source of truth referenced by both UI and service (per [`TRD.md`](TRD.md) §6). |
| `SessionContext` | Holds the currently logged-in user for the running client (in-memory, single-user desktop session — no token/JWT machinery needed). |

### `aura.exception`
| Class | Responsibility |
|---|---|
| `AuraException` | Base unchecked exception for all application-level errors. |
| `InvalidCredentialsException` | Login failed / bad TKMCE domain. |
| `UnauthorizedActionException` | A service method's role check failed. |
| `DuplicateHypeException` | A student tried to hype the same submission twice. |

## 4. Request/response flow

```
User → ui (Swing) → service (validation + rules) → dao (JDBC) → MySQL
User ← ui (Swing) ← service (mapped result / exception) ← dao (ResultSet) ← MySQL
```

Exceptions from `aura.dao` (e.g. `SQLException`) are caught at the `aura.service` boundary and re-thrown as an `aura.exception` type — `java.sql.SQLException` should never propagate up to `aura.ui`. This is also what the "Exception handling" row in `TRD.md`'s course-concept table refers to.

## 5. Anonymity boundary (architectural law)

This is the single most important rule in this document, restated from [`AGENT.md`](../AGENT.md):

> Any DAO or service method reachable from `aura.ui.admin.*` must never `SELECT` or join `submissions.student_id`. Only `SubmissionDAO.findMySubmissions(int studentId)`, called exclusively from `TrackingService` on behalf of the currently logged-in student, may touch that column.

Concretely:
- `AdminSubmissionService` and everything under `aura.ui.admin` are built against a `SubmissionDAO` query set that simply never returns `student_id` — not "returns it but the UI hides it." The column isn't in the result at all on that path.
- `TrackingService.findMySubmissions()` takes no external `studentId` parameter from the UI layer — it reads the id from `SessionContext` itself, so there's no way to call it with someone else's id even by mistake.

See [`DATABASE.md`](DATABASE.md) for the schema-level reasoning and [`SECURITY.md`](SECURITY.md) for the honest limits of this guarantee.
