# AURA — Phase 2 Presentation & Viva Defense Manual

> **Academic Defense Guide for Presentation II**  
> **Course:** KTU CST205 / CSL203 — Object Oriented Programming in Java  
> **Department:** Computer Science & Engineering, TKM College of Engineering (TKMCE)  
> **Evaluation Rubrics Target:** **16 / 16 Marks** (R1: 5/5, R2: 4/4, R3: 4/4, R4: 3/3)  
> **Presentation Engine:** [`docs/presentation/index.html`](index.html) (Editorial-Light Academic White Theme)  
> **Total Time Allotted:** **20 Minutes** (~3.5 to 4 Minutes per Member)

---

## 1. Executive Presentation Briefing

### 1.1 Team Roles & Allocated Minutes
| Order | Speaker | Roll No | Module Responsibility | Time |
| :---: | :--- | :---: | :--- | :---: |
| **1** | **Muhammed Rinshid VP** *(Lead)* | B25CSB45 | Introduction, Architecture, Live Demo Driver, Connection Pooling & Conclusion | **5.0 mins** |
| **2** | **Mohammed Nafih** | B25CSB37 | Security Pipeline, Google OAuth 2.0 PKCE, BCrypt & Domain Gating | **3.5 mins** |
| **3** | **Nirmal Binoy** | B25CSB52 | Crowd Urgency Hype Engine, Composite UNIQUE Keys & Domain Models | **3.5 mins** |
| **4** | **Rahandeep RD** | B25CSB53 | DAO Layer Architecture, PreparedStatements & Atomic Transactions | **3.5 mins** |
| **5** | **Athil Rahuman A** | B25CSB84 | 5-State Lifecycle, Resolution Notes, RFC-4180 CSV & JUnit 5 Testing | **3.5 mins** |
| — | **All Members** | — | Faculty Examiner Viva Voce Defense | **Remaining** |

### 1.2 Presentation Keyboard Shortcuts
When projecting [`docs/presentation/index.html`](index.html) in the classroom:
- `→` / `Space` / `PageDown`: Advance to next slide.
- `←` / `PageUp`: Return to previous slide.
- `G`: **Toggle Slide Overview Grid** (Instantly jump to any slide if a faculty examiner asks about a specific diagram).
- `S`: **Toggle Speaker Notes Drawer** (Shows word-for-word talking points; keep closed during audience presentation).
- `F`: **Toggle Fullscreen Mode** (Fills entire projector screen cleanly with no browser address bar).
- `T`: **Cycle Themes** (Default is `editorial-light` crisp white; cycles through `cyber-dark` and `executive`).
- `⏱` (Click on HUD timer): Starts/pauses 20-minute countdown tracker.

---

## 2. Word-for-Word Spoken Scripts

---

### 🎙️ Speaker 1: Muhammed Rinshid VP (Team Lead & System Coordinator)
**Total Time:** ~5 Minutes (Split across Opening, Demo, Individual Deep Dive, and Closing)  
**Slides Owned:** 1–8, 9–10 (Lead), 11–13, 26–28

#### Part A: Opening & Macro Architecture (Slides 1–8) — *~2 Minutes*
> **[Slide 1: Title & Team Introduction]**  
> *"Respected evaluators, faculty members, and fellow students, good morning. I am Muhammed Rinshid VP, Team Lead of Project AURA: Autonomous University Response and Action. Alongside my teammates Mohammed Nafih, Nirmal Binoy, Rahandeep RD, and Athil Rahuman A from the Department of Computer Science and Engineering at TKM College of Engineering, we are proud to present Phase 2 of our project.*  
> *Today, we demonstrate a production-ready, pure Java SE 17 enterprise system connected to a live cloud PostgreSQL database on Supabase that solves campus grievance redressal through an impenetrable Decoupled Anonymity Vault."*

> **[Slide 2: Problem Tension & UN SDGs]**  
> *"Across university campuses, infrastructure and lab issues are reported through informal WhatsApp groups, physical suggestion boxes, or flat Google Forms. These channels fail because they offer zero tracking, no transparent priority queue, and students fear academic reprisal when reporting institutional deficiencies.  
> AURA replaces this broken status quo with a mathematical privacy paradigm: The Decoupled Anonymity Vault. In addition, community upvoting bubbles urgent issues to the top of the admin queue, directly addressing UN Sustainable Development Goals 16 for institutional transparency, 11 for campus safety, and 4 for quality education."*

> **[Slide 3: Project Progress & Implementation (Rubric R1)]**  
> *"Directly addressing Rubric Criteria R1 on Project Progress: As of today, our Phase 2 MVP is 85% fully completed and signed off. We have built 6 cloud relational tables in Supabase PostgreSQL 17.6, a 100% pure Java SE 17 desktop interface using FlatDarkLaf, native Google OAuth 2.0 PKCE authentication with local loopback servers, and an automated JUnit 5 test suite with 14 automated tests achieving a 100% pass rate with zero regressions."*

> **[Slide 4: 4-Tier Enterprise Architecture]**  
> *"Architecturally, AURA follows a strict 4-Tier enterprise model:  
> 1. The Presentation Layer in `aura.ui` handles user interaction via modern Swing and FlatLaf with zero direct SQL calls.  
> 2. The Service Layer in `aura.service` orchestrates business logic, RBAC checks, and the Anonymity Vault.  
> 3. The DAO Layer in `aura.dao` manages JDBC PreparedStatements and transaction boundaries.  
> 4. The Cloud Database Layer in Supabase PostgreSQL enforces relational integrity and cascade rules."*

> **[Slide 5 & 6: Anonymity Vault & Tech Stack]**  
> *"The heart of AURA is our Decoupled Anonymity Vault. In the `submissions` table, there is NO `student_id` column. Only the private `student_submission_receipts` table maps tickets to owners during an authenticated student's session.  
> Our technology choices were deliberate: Java SE 17 for modern records and HTTP clients, FlatLaf for high-DPI desktop rendering, Supabase PostgreSQL for ACID compliance, and BCrypt for cryptographic security."*

> **[Slide 7: Five-Member Code Ownership Matrix (Rubric R3)]**  
> *"Every member of our team has written actual Java code committed in our Git repository. I manage the core architecture and connection pooling; Nafih handles security and OAuth; Nirmal owns the Hype crowd voting engine; Rahandeep built the DAO layer and atomic transactions; and Athil built the 5-state lifecycle and test suite."*

#### Part B: The Live Software Demonstration (Slides 8–10) — *~2 Minutes*
*(Rinshid switches to live running application on screen and narrates the Golden Path while teammates stand ready)*  
> **[Slide 8, 9 & 10: Live Demo Protocol]**  
> *"We will now demonstrate our live software connected in real-time to Supabase cloud database:  
> - **Step 1 (Auth):** I log in using our institutional Google OAuth flow. The browser opens, authenticates with `@tkmce.ac.in`, and our Java loopback server completes the PKCE handshake.  
> - **Step 2 (Submit):** In the Student Portal, I report a broken projector in CSE Lab 3. Notice the ticket appears in the public feed with zero author metadata, while my personal receipt is safely stored in my vault.  
> - **Step 3 (Hype):** A second student opens their portal, spots the projector issue, and clicks 'Hype'. The count increments from 0 to 1, bubbling it up the queue.  
> - **Step 4 (Admin):** Our campus administrator opens the Admin Governance window, sees the prioritized ticket, updates the status to IN_PROGRESS, and attaches an official note: 'Technician dispatched to Lab 3'.  
> - **Step 5 (Redressal):** When the student refreshes their 'My Submissions' tab, the badge turns blue and the admin's note is instantly visible. The grievance loop is closed."*

#### Part C: Rinshid's Individual Technical Deep Dive (Slides 11–13) — *~1.5 Minutes*
> **[Slide 11 & 12: Startup Lifecycle & Multi-Host Failover]**  
> *"Presenting my individual technical contribution: I engineered the system startup lifecycle in `aura.Main` and the connection factory in `DatabaseConfig.java`.  
> Addressing Rubric R2 on Technical Problem Solving: While testing against Supabase cloud over college Wi-Fi, we experienced intermittent DNS resolution drops. To resolve this independently, I configured a multi-host failover JDBC URL: `aws-0-ap-northeast-1.pooler.supabase.com` paired with direct AWS IP `54.64.190.72`. If domain lookup hangs, the driver transparently fails over to the direct IP without interrupting the session. Furthermore, I implemented thread-safe session tracking in `SessionContext`."*

> **[Slide 13: Handover]**  
> *"I now hand over to Mohammed Nafih to present our Security and Google OAuth PKCE architecture."*

---

### 🎙️ Speaker 2: Mohammed Nafih (Security & Anonymity Vault Lead)
**Total Time:** ~3.5 Minutes  
**Slides Owned:** 14, 15, 16  
**Classes Owned:** `aura.service.GoogleOAuthService`, `aura.service.AuthService`, `aura.util.PasswordUtil`, `aura.util.ValidationUtil`

#### Speaking Script (Nafih)
> **[Slide 14: RFC 8252 Google OAuth 2.0 PKCE Engine]**  
> *"Respected examiners, I am Mohammed Nafih, Security and Anonymity Vault Lead.  
> In a native desktop application, embedding hardcoded client secrets is a severe security vulnerability. To solve this, I implemented RFC 8252: OAuth 2.0 for Native Apps with PKCE (Proof Key for Code Exchange).  
> In `GoogleOAuthService.java`, when a student signs in, we generate a cryptographically random 48-byte `code_verifier` using `SecureRandom` and hash it using SHA-256 to create the `code_challenge`. We spin up an ephemeral loopback `HttpServer` from `jdk.httpserver` on `127.0.0.1`, launch the default system browser, and validate the returned authorization code against our CSRF state token using constant-time string comparison. This eliminates authorization code interception attacks."*

> **[Slide 15: BCrypt Password Hashing & Institutional Domain Gating]**  
> *"For users choosing password authentication, security must remain impregnable. In `PasswordUtil.java`, I implemented `jBCrypt` salted hashing with a Work Factor of 10 (1,024 rounds). Passwords are never stored in plaintext and never touch exception logs.  
> Furthermore, in `ValidationUtil.java`, we enforce strict institutional regex validation: only emails matching `^[A-Za-z0-9._%+-]+@tkmce\\.ac\\.in$` can authenticate or register. We also pass the `hd=tkmce.ac.in` hosted domain parameter to Google OAuth, ensuring personal non-institutional Gmail accounts are rejected at the door."*

> **[Slide 16: Technical Challenges & Handover]**  
> *"Addressing Rubric R2 on Problem Solving: Executing an OAuth loopback server on the Swing Event Dispatch Thread (EDT) would freeze the desktop UI while waiting for the browser. I resolved this by wrapping the authentication flow in a Java `SwingWorker`, keeping the UI responsive with a progress indicator until the token exchange completes.  
> I now pass the presentation to Nirmal Binoy to explain our Crowd Urgency Hype Engine."*

---

### 🎙️ Speaker 3: Nirmal Binoy (Core Developer — Hype Engine & Modeling)
**Total Time:** ~3.5 Minutes  
**Slides Owned:** 17, 18, 19  
**Classes Owned:** `aura.service.HypeService`, `aura.dao.SubmissionHypeDAO`, `aura.model.Submission`, `aura.model.User`  
*(Designed to be simple, crisp, confident, and high-scoring)*

#### Speaking Script (Nirmal)
> **[Slide 17: Crowd Urgency & Hype Prioritization Engine]**  
> *"Respected examiners, I am Nirmal Binoy, Core Developer for the Hype Engine and Data Modeling.  
> In our college, hundreds of maintenance and lab issues arise each week. The critical question is: How does the administration know which problem is most urgent?  
> We solved this by building the Hype Engine. Inspired by community upvoting platforms like Reddit, AURA allows students to 'hype' issues that impact the campus. If 30 students upvote a broken water purifier on the second floor, that issue automatically bubbles to the top of the administrator's triage feed. I implemented this in `HypeService.java` and `SubmissionHypeDAO.java`, while encapsulating domain properties in `Submission.java` and `User.java`."*

> **[Slide 18: Double-Vote Prevention & Database Idempotency]**  
> *"Addressing Rubric R2 on Technical Problem Solving: A crucial technical challenge in any voting system is preventing vote manipulation and double-voting. A student must not be able to artificially inflate priority by clicking hype multiple times.  
> To solve this with mathematical certainty, I designed a composite UNIQUE constraint in PostgreSQL:  
> `CONSTRAINT uq_submission_student UNIQUE (submission_id, student_id)`.  
> In SQL, we use `ON CONFLICT (submission_id, student_id) DO NOTHING`. In Java, our `toggleHype()` method checks if the student has already voted. If yes, it removes the upvote; if no, it inserts the upvote. This guarantees mathematical idempotency: exactly one vote per student per ticket."*

> **[Slide 19: Technical Challenges & Handover]**  
> *"To ensure optimal database performance, `SubmissionDAO` calculates total hypes using an optimized `LEFT JOIN` with `COUNT(hype_id)` grouped by submission ID in a single SQL roundtrip. In our tests, `HypeServiceTest` verifies that duplicate votes never corrupt the count.  
> I now hand over to Rahandeep RD to present our DAO Persistence Layer and Atomic Transactions."*

---

### 🎙️ Speaker 4: Rahandeep RD (Database & GUI Engineer)
**Total Time:** ~3.5 Minutes  
**Slides Owned:** 20, 21, 22  
**Classes Owned:** `aura.dao.SubmissionDAO`, `aura.dao.UserDAO`, `aura.dao.StudentReceiptDAO`, `sql/schema.sql`  
*(Designed to be simple, crisp, confident, and high-scoring)*

#### Speaking Script (Rahandeep)
> **[Slide 20: DAO Layer Architecture & JDBC PreparedStatements]**  
> *"Respected examiners, I am Rahandeep RD, Database and GUI Engineer.  
> In software architecture, allowing UI components to execute raw SQL directly is a dangerous anti-pattern that causes SQL injection and connection leaks.  
> I built the Data Access Object (DAO) layer in package `aura.dao`, including `SubmissionDAO` and `UserDAO`. Every database interaction uses parameterized `PreparedStatements` with Java 7 `try-with-resources`. User inputs are bound strictly as data parameters, making SQL injection impossible and guaranteeing that database connections and statements are closed immediately after execution."*

> **[Slide 21: ACID Transactions in the Decoupled Anonymity Vault]**  
> *"Addressing Rubric R2 on Technical Problem Solving: When a student submits a new ticket, two separate database rows must be created: the public anonymous ticket in `submissions`, and the private receipt in `student_submission_receipts`.  
> What happens if the network drops right after the ticket is created? In naive code, the public ticket would exist, but the student receipt would fail, leaving an orphaned ticket that the student can never track!  
> To solve this, I implemented an atomic ACID transaction in `createWithReceipt()`:  
> We disable autocommit with `connection.setAutoCommit(false)`, insert the submission, retrieve the generated key, insert the student receipt, and execute `connection.commit()`. If an exception occurs at any point, `connection.rollback()` executes. This guarantees 100% data consistency."*

> **[Slide 22: Technical Challenges & Handover]**  
> *"Furthermore, in `findByIds()`, I avoided the N+1 query problem by building a dynamic parameterized `IN (?, ?, ...)` clause, fetching all student tickets in a single database query.  
> I now pass the presentation to Athil Rahuman A to present our Resolution Lifecycle, CSV Reporting, and JUnit 5 Testing Suite."*

---

### 🎙️ Speaker 5: Athil Rahuman A (Audit, Reports & Testing Lead)
**Total Time:** ~3.5 Minutes  
**Slides Owned:** 23, 24, 25  
**Classes Owned:** `aura.service.AdminSubmissionService`, `aura.service.ReportService`, `aura.dao.ResolutionNoteDAO`, `src/test/java/aura/...`

#### Speaking Script (Athil)
> **[Slide 23: 5-State Resolution Lifecycle & Audit Trail]**  
> *"Respected examiners, I am Athil Rahuman A, Audit, Reports, and Testing Lead.  
> A campus grievance system without a clear resolution workflow fails to hold administration accountable. I implemented the 5-State Resolution Lifecycle in `AdminSubmissionService.java` and `ResolutionNoteDAO.java`.  
> Submissions advance through a deterministic state machine: `PENDING` ➔ `ASSIGNED` ➔ `IN_PROGRESS` ➔ `RESOLVED` or `REJECTED`. Invalid transitions, such as attempting to resolve a pending issue without assignment, are rejected with an `IllegalArgumentException`. Every state change logs the admin ID and timestamp in `submission_history`, creating an immutable audit trail."*

> **[Slide 24: RFC-4180 CSV Export & Automated JUnit 5 Suite]**  
> *"For institutional leadership and accreditation bodies like NAAC and NBA, verifiable reporting is mandatory. In `ReportService.java`, I implemented RFC-4180 compliant CSV export, escaping delimiters and multiline text using an in-memory `StringBuilder`.  
> In addition, I led our automated testing strategy. We engineered a 14-test JUnit 5 suite executed via Maven Surefire, testing authentication, DAO queries, Hype idempotency, and state transitions with a 100% pass rate."*

> **[Slide 25: Reflection-Based Anonymity Regression Testing (Rubric R2)]**  
> *"Addressing Rubric R2 on Technical Problem Solving: How can we mathematically prove to faculty examiners that administrative code will NEVER leak student identity?  
> I wrote `AdminAnonymityRegressionTest.java` using Java Reflection. During every Maven build, this test inspects all public methods of `Submission.class` and asserts that `getStudentId` or `setStudentId` do not exist. If any future developer accidentally adds a student ID column to the public model, `mvn test` immediately fails. This provides automated, verifiable proof of our Anonymity Vault.  
> I now hand back to our Team Lead, Muhammed Rinshid, to present our project milestones and conclude."*

---

### 🎙️ Speaker 1: Muhammed Rinshid VP (Closing & Conclusion)
**Total Time:** ~1.5 Minutes  
**Slides Owned:** 26, 27, 28

#### Speaking Script (Rinshid)
> **[Slide 26: Planned vs. Completed Work Matrix (Rubric R4)]**  
> *"Directly fulfilling Rubric Criteria R4 on Progress Presentation: Our Planned versus Completed Matrix shows that all 7 core Phase 2 milestones—Cloud database, Anonymity Vault, OAuth PKCE, Hype Engine, FlatLaf GUI, 5-state lifecycle, and JUnit 5 suite—are 100% completed and demonstrated live today. The remaining 15% consists of Phase 3 enhancements: push notifications and college server deployment."*

> **[Slide 27 & 28: Empirical Proof & Conclusion]**  
> *"In conclusion, AURA is a verified, production-grade software system engineered with strict Object-Oriented principles in Java SE 17. On behalf of Muhammed Rinshid, Mohammed Nafih, Nirmal Binoy, Rahandeep RD, and Athil Rahuman, thank you for your time. We are now delighted to answer your questions during the viva voce."*

---

## 3. Faculty Examiner Viva Voce Defense Handbook (15 Q&As)

### Member 1: Muhammed Rinshid VP (Architecture & Infrastructure)
1. **Q: Why use a Singleton pattern for `DatabaseConfig`?**  
   *A:* "Creating a new database connection for each query takes 200–400 ms over TLS and would quickly exhaust Supabase connection pool limits. A Singleton connection factory ensures connections are pooled, thread-safe, and reused, reducing query latency to ~5 ms."
2. **Q: How does the multi-host failover URL work?**  
   *A:* "The PostgreSQL JDBC driver accepts multiple comma-separated hosts in the connection string: `aws-0-ap-northeast-1.pooler.supabase.com:5432,54.64.190.72:5432`. If the primary hostname fails to resolve due to campus DNS timeouts, the driver automatically fails over to the AWS direct IP."
3. **Q: How does `SessionContext` prevent privilege escalation?**  
   *A:* "`SessionContext` stores the authenticated `User` object in thread-safe memory. Role checks invoke `user.getRole() == Role.ADMIN` against immutable enums, preventing client-side role manipulation."

### Member 2: Mohammed Nafih (Security & OAuth)
1. **Q: What is PKCE and why is it necessary for desktop apps?**  
   *A:* "In public desktop clients, client secrets cannot be securely stored. PKCE (Proof Key for Code Exchange, RFC 7636) dynamically generates a `code_verifier` and SHA-256 `code_challenge`. Even if an attacker intercepts the authorization code on localhost, they cannot exchange it without the original verifier."
2. **Q: How is the local loopback server protected against hijacking?**  
   *A:* "We bind `HttpServer` strictly to `127.0.0.1` on a random high-order port. We generate a cryptographically random 32-byte CSRF `state` token and verify it upon callback using constant-time string comparison (`MessageDigest.isEqual`)."
3. **Q: What is the BCrypt Work Factor and why choose 10?**  
   *A:* "The work factor determines the number of hashing rounds ($2^{10} = 1,024$). Work factor 10 takes ~80 ms on modern hardware—fast enough for instant student login, but computationally prohibitive for brute-force attacks."

### Member 3: Nirmal Binoy (Hype Engine & Modeling)
1. **Q: How does the database prevent a student from voting twice?**  
   *A:* "In `sql/schema.sql`, the `submission_hype` table has a composite unique constraint: `CONSTRAINT uq_submission_student UNIQUE (submission_id, student_id)`. Any attempt to insert a duplicate pair violates this constraint and is rejected by PostgreSQL."
2. **Q: Explain the `toggleHype` logic in Java.**  
   *A:* "In `HypeService`, we query `hasStudentHyped()`. If `true`, we call `removeHype()` (deleting the row and decrementing count); if `false`, we call `addHype()` (inserting the row). This gives a clean toggle experience."
3. **Q: Why doesn't `Submission.java` contain a reference to `User.java`?**  
   *A:* "To enforce the Decoupled Anonymity Vault at the domain model level. `Submission` contains only ticket attributes (title, category, priority, status). The student relationship is held in `StudentReceiptDAO`, ensuring zero author data is ever passed to admin views."

### Member 4: Rahandeep RD (DAO Layer & Persistence)
1. **Q: Why use `PreparedStatement` instead of regular `Statement`?**  
   *A:* "Two reasons: First, security: PreparedStatements pre-compile SQL on the database engine and bind inputs as literals, preventing SQL injection. Second, performance: pre-compiled statements can be cached and reused efficiently."
2. **Q: Explain how `createWithReceipt()` guarantees ACID atomicity.**  
   *A:* "We call `conn.setAutoCommit(false)`. We insert the submission, get the generated key, and insert the student receipt. If both succeed, `conn.commit()` commits the changes. If either fails, `conn.rollback()` executes, guaranteeing zero orphaned tickets."
3. **Q: How does `findByIds()` prevent the N+1 query problem?**  
   *A:* "Instead of executing $N$ separate SQL queries for $N$ tickets, `findByIds()` dynamically constructs a parameterized `WHERE submission_id IN (?, ?, ...)` clause, fetching all records in a single database roundtrip."

### Member 5: Athil Rahuman A (Lifecycle, Reports & Testing)
1. **Q: How is an invalid status transition rejected?**  
   *A:* "In `AdminSubmissionService`, we maintain a transition map. `PENDING` can only move to `ASSIGNED` or `REJECTED`. If an admin attempts to move directly from `PENDING` to `RESOLVED`, the method throws an `IllegalArgumentException`."
2. **Q: How does `AdminAnonymityRegressionTest` work?**  
   *A:* "It uses Java Reflection to inspect all public methods of `Submission.class`. It streams through method names and asserts that none match `getStudentId` or `setStudentId`. If anyone adds a student ID to the public model, `mvn test` immediately fails."
3. **Q: How does `ReportService` comply with RFC-4180?**  
   *A:* "It checks each field for commas, double quotes, or newlines. If present, it wraps the field in double quotes and escapes existing quotes by doubling them (`""`), ensuring accurate parsing in Microsoft Excel and external data tools."

---

## 4. Rehearsal Checklist (For Tonight)
- [ ] Every member practices their script 3 times with a stopwatch to stay under **3.5 minutes**.
- [ ] Nirmal and Rahandeep review their 3 specific viva questions and feel 100% confident.
- [ ] Rinshid tests opening [`docs/presentation/index.html`](index.html) in Chrome or Edge and tests pressing `G`, `S`, and `F`.
- [ ] Team coordinates the Golden Path live demo handoffs so screen transitions are seamless.
- [ ] Target tomorrow: **16 / 16 Full Marks**.
