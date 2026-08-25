# Product Requirements Document (PRD) — AURA

**Autonomous University Response and Action**
*"Report. Track. Resolve. Improve."*

Department of Computer Science & Engineering, TKM College of Engineering (TKMCE)

## 1. Problem statement

Campus issues at TKMCE currently travel through informal, untracked channels:

| Channel | Centralized? | Live tracking? | Role security? | Primary limitation |
|---|---|---|---|---|
| WhatsApp / messaging | No | No | No | Unstructured, messages lost in group chats |
| Paper suggestion boxes | Partial | No | No | Delayed collection, zero feedback to the student |
| Google Forms | Yes | No | Basic | Flat sheet, no resolution lifecycle |

Three consequences follow directly from this:

1. **Students don't report** minor-but-real problems (a broken projector, faulty Wi-Fi) because there's no visible payoff — nothing shows the report was seen, let alone fixed.
2. **The problems that get attention are the loudest, not the most common.** A single vocal complaint to the right person can jump the queue while a problem affecting 200 students goes unreported anywhere central.
3. **Admin staff have no aggregated view** of what's actually recurring across campus, so maintenance stays reactive instead of prioritized by real impact.

## 2. What AURA is

A single desktop platform (Java Swing) where:

- Students **report problems and suggestions anonymously** — no name attached, ever, not even to admin.
- Other students can **hype** (upvote) a report they also care about, surfacing the most-affecting problems to the top of a **Trending** view — this is the direct fix for consequence #2 above.
- Admin staff **triage, assign, and resolve** reports through a tracked status pipeline, with resolution notes visible back to students.
- Every student can always see the status of **their own** reports, even though nobody — including admin — can trace a report back to who posted it.

This is **not** a public app. It is scoped to one institution: only verified TKMCE identities (`@tkmce.ac.in`) can register or log in.

## 3. Users

### Student
- Submits an **Issue** (a problem) or a **Suggestion** (an improvement idea), anonymously.
- Sets a priority (Low / Medium / High) and a short title + description.
- Browses other students' submissions, sorted by **Trending** (hype count) or **Recent**.
- Hypes submissions they also experience or support — one hype per student per submission.
- Views **My Submissions**: their own reports and current status, regardless of who else can see it.
- Reads resolution notes once an admin resolves their report.

### Admin
- Reviews the incoming queue, sorted by priority/hype/date.
- Assigns a submission to a responsible staff area and updates its status (`ASSIGNED` → `IN_PROGRESS`).
- Adds a resolution note and marks a submission `RESOLVED` (or `REJECTED` with a reason).
- Generates a simple report (CSV export) of submissions/resolutions for record-keeping.
- **Cannot** see who authored any submission — the anonymity guarantee applies to admin too (see [`SECURITY.md`](SECURITY.md) for exactly how, and its honest limits).

## 4. Core user stories

1. As a student, I can submit a problem or suggestion without my identity being attached to it, so I can report sensitive issues without hesitation.
2. As a student, I can see how many other students hyped a submission, so I know if a problem is affecting others too.
3. As a student, I can hype a submission once, so the count reflects real distinct support, not repeat clicks.
4. As a student, I can see the status and any resolution note for reports I personally submitted, so I know something actually happened.
5. As an admin, I can see all submissions sorted by priority, hype, or recency, so I triage by real impact, not by who shouted loudest.
6. As an admin, I can assign, update status, and record a resolution note, so there's an auditable trail per submission.
7. As an admin, I can export a report of submissions/resolutions, so campus maintenance decisions are backed by data.

## 5. Non-goals for this version

Kept out deliberately, to stay demoable within the project's real timeline (see [`ROADMAP.md`](ROADMAP.md)) and defensible as a course project rather than an open-ended product:

- No comment threads or replies on submissions (hype only — a single, simple signal).
- No push notifications / email alerts.
- No mobile app — desktop Swing client only.
- No multi-institution support — TKMCE only, by design (see login gate in [`SECURITY.md`](SECURITY.md)).
- No public/unauthenticated access — every screen requires a logged-in TKMCE identity.

## 6. Success criteria

The project is successful when a single, uninterrupted walkthrough works end-to-end:

1. A student registers/logs in with a TKMCE identity, submits an issue, and sees it appear in their "My Submissions" list as `PENDING`.
2. A second student logs in, sees the same issue in the Trending/Recent list (with no author name), and hypes it.
3. An admin logs in, sees the issue in the queue (again, with no author name), assigns it, moves it through `IN_PROGRESS`, and resolves it with a note.
4. The original student's dashboard reflects `RESOLVED` and shows the resolution note.
5. At no point in this walkthrough does any admin-facing screen, query log, or exported report reveal which student authored the issue.
