# Roadmap — AURA

This is **not** the PPT's original week-by-week academic pacing. The team is building this at maximum speed with agent assistance, not spreading it across a full semester, so this roadmap is a **dependency-ordered milestone list**, not a calendar. Each milestone is a working, demoable increment — the next one doesn't start until the current one compiles, is tested (per [`TESTING.md`](TESTING.md)), and every team member can explain every class in it (per [`AGENT.md`](../AGENT.md) rule 9).

## Milestones

### 1. Database schema
- Finalize and run [`../sql/schema.sql`](../sql/schema.sql) (all five tables, including `submission_hype`).
- Load [`../sql/seed.sql`](../sql/seed.sql) for manual sanity-checking.
- **Depends on:** [`DATABASE.md`](DATABASE.md) being final.
- **Done when:** schema applies cleanly to a fresh MySQL instance with no errors.

### 2. Model & enum classes
- `aura.model.*`, `aura.enums.*` — plain data classes matching the schema exactly.
- **Depends on:** milestone 1.
- **Done when:** every column in every table has a corresponding field, and every `ENUM` in the schema has a corresponding Java enum.

### 3. DAO layer
- `UserDAO`, `SubmissionDAO`, `SubmissionHistoryDAO`, `ResolutionNoteDAO`, `SubmissionHypeDAO`.
- Includes the admin-facing vs. student-facing query split described in [`ARCHITECTURE.md`](ARCHITECTURE.md) §5.
- **Depends on:** milestone 2.
- **Done when:** each DAO has a passing test class per [`TESTING.md`](TESTING.md) §2, including the anonymity-boundary check.

### 4. Service layer
- `AuthService`, `SubmissionService`, `TrackingService`, `HypeService`, `AdminSubmissionService`, `ReportService`.
- Includes the TKMCE-domain login gate and the role re-checks on every admin method.
- **Depends on:** milestone 3.
- **Done when:** each service has a passing test class, including `AdminAnonymityBoundaryTest`, the duplicate-hype test, and the TKMCE-domain rejection test.

### 5. Swing UI
- Build order: `LoginFrame` (TKMCE-gated) → `StudentDashboardFrame` (Trending/Recent/My Submissions) → `SubmissionFormPanel` → `AdminDashboardFrame` (queue, assign, resolve) → `ReportPanel` (CSV export).
- **Depends on:** milestone 4.
- **Done when:** every screen is wired to its service layer only (never directly to a DAO — see [`ARCHITECTURE.md`](ARCHITECTURE.md) §1), and each screen matches its approved mockup.

### 6. Integration & golden-path walkthrough
- Run the full walkthrough from [`PRD.md`](PRD.md) §6 end-to-end: student submits anonymously → second student hypes it → admin triages and resolves it → original student sees the resolution — with no point in the flow leaking author identity to admin.
- Run the full test suite (`mvn test`) green.
- **Depends on:** milestone 5.
- **Done when:** the walkthrough succeeds on a clean database and every test passes.

## What's explicitly not scheduled

Per [`TRD.md`](TRD.md) §4 and [`PRD.md`](PRD.md) §5 — not on this roadmap for v1: comment threads, notifications, mobile client, multi-institution support, connection pooling, caching, the cryptographic tracking-token anonymity scheme. These are legitimate "future work" answers for a viva, not partially-built features sitting in the codebase.
