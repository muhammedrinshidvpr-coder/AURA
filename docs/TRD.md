# Technical Requirements Document (TRD) — AURA

**Autonomous University Response and Action**  
Department of Computer Science & Engineering, TKM College of Engineering (TKMCE)  
Academic Mapping: KTU CST205 (OOP Using Java) & CSL203 (OOP Lab in Java)  
Presentation Reference: [`docs/AURA.pdf`](AURA.pdf) (Slides 2, 12, 14, 15)

---

## 1. Approved Technical Stack & Justification

The technical stack is purposefully chosen to align with the KTU university syllabus, fulfill the requirements presented in Slide 15 of [`docs/AURA.pdf`](AURA.pdf), and deliver a polished, commercial-grade presentation:

| Concern | Approved Choice | Technical Justification & Rubric Defense |
|---|---|---|
| **Programming Language** | **Java 17 (SE)** | Primary language for KTU CS curriculum; modern LTS release with records, pattern matching, and enhanced switch expressions. |
| **Desktop GUI Engine** | **Java Swing + FlatLaf 3.5+** | Eliminates the outdated 1990s Swing look. FlatLaf provides modern dark/light flat themes, crisp vector icons, rounded components, and high-DPI scaling while strictly complying with the Java SE requirement. |
| **Database Engine** | **Supabase Cloud PostgreSQL 15+** | Approved in Slide 15 (*"MySQL / PostgreSQL Database Engine"*). Cloud hosting enables remote team collaboration, automated backups, TLS encryption, and supports the Decoupled Anonymity Vault. |
| **Persistence Layer** | **JDBC (`org.postgresql.Driver`)** | Direct JDBC DAO pattern using `PreparedStatement` only. Zero ORM magic; every SQL operation is line-by-line explainable to faculty examiners. |
| **Password Hashing** | **jBCrypt 0.4** | Industry standard BCrypt key derivation function; defeats rainbow-table and dictionary attacks via salting. |
| **Build & Dependency** | **Apache Maven 3.9+** | Declarative dependency management (`pom.xml`) standard across academic and professional Java environments. |
| **Testing Framework** | **JUnit 5 (Jupiter)** | Standard automated testing framework for DAO, Service, and Anonymity regression suites. |

---

## 2. Functional Requirements (FR)

| ID | Requirement Specification | Primary Actor | Approved Design Reference |
|---|---|---|---|
| **FR-1** | Institutional registration and authentication via `@tkmce.ac.in` domain. | Student, Admin | Slide 7 (UC-1), Slide 20 |
| **FR-2** | Anonymous submission of campus complaints (`ISSUE`) or proposals (`SUGGESTION`). | Student | Slide 7 (UC-2, UC-3), Slide 22 |
| **FR-3** | Categorization into designated campus departments (IT, Electrical, Civil, Labs, Hostel, General). | Student | Slide 6, Slide 22 |
| **FR-4** | Specific campus location tagging (e.g., "CSE Lab 3", "Civil Block 2nd Floor") and photo attachment URL. | Student | Slide 22 |
| **FR-5** | Real-time crowd upvoting ("Hype") with duplicate vote prevention. | Student | Slide 6, Slide 14 |
| **FR-6** | Browse submissions sorted by **Trending** (hype count) or **Recent** (chronological). | Student, Admin | Slide 21 |
| **FR-7** | Isolated "My Submissions" tracker via the Private Receipt Vault. | Student | Slide 7 (UC-4), Slide 21 |
| **FR-8** | Administrative queue review with multi-criteria filtering (Category, Status, Priority). | Admin | Slide 7 (UC-9), Slide 23 |
| **FR-9** | Ticket assignment and 5-state lifecycle progression (`PENDING → ASSIGNED → IN_PROGRESS → RESOLVED / REJECTED`). | Admin | Slide 7 (UC-11, UC-12), Slide 23 |
| **FR-10** | Recording official resolution notes and attaching completion details. | Admin | Slide 7 (UC-13), Slide 23 |
| **FR-11** | Administrative metrics calculation (Active tickets, Resolved counts, SLA benchmarks). | Admin | Slide 7 (UC-14), Slide 21 |
| **FR-12** | Exporting structured CSV audit reports for estate and maintenance archives. | Admin | Slide 7 (UC-15) |

---

## 3. Non-Functional Requirements (NFR)

- **NFR-1 (Password Security):** Plaintext passwords must never be stored in the database or retained in memory beyond authentication. BCrypt hashing with work factor 10 is mandatory.
- **NFR-2 (SQL Injection Immunity):** Zero SQL string concatenation. All database interactions must use parameterized `PreparedStatement` objects exclusively.
- **NFR-3 (Cryptographic & Physical Anonymity):** The `submissions` table must never contain foreign keys or attributes referencing the author. Ownership must be isolated within the `student_submission_receipts` vault.
- **NFR-4 (Vote Integrity):** Database-level uniqueness constraints (`UNIQUE (submission_id, student_id)`) enforce that a student may only hype an issue once.
- **NFR-5 (Domain Gating):** Strict regex domain validation rejects any email address not ending in `@tkmce.ac.in`.
- **NFR-6 (Responsive High-DPI UI):** Desktop GUI components must scale crisply on modern 1080p and 4K displays using FlatLaf vector styling.

---

## 4. Course Concept Coverage (KTU CST205 / CSL203 Syllabus)

When examiners ask: *"What syllabus concepts does your project demonstrate?"*, the team references this exact mapping:

| KTU Syllabus Concept | Implementation in AURA |
|---|---|
| **Inheritance & Polymorphism** | Polymorphic user hierarchy (`User → Student, Admin`) and submission models (`Submission → Issue, Suggestion`). |
| **Exception Handling** | Custom checked and unchecked exception hierarchy (`AuraException`, `InvalidCredentialsException`, `UnauthorizedActionException`, `DuplicateHypeException`) ensuring raw SQL errors never escape to the UI. |
| **Java Collections Framework** | Dynamic in-memory management using `List<Submission>`, `Map<String, Object>` for dashboard metrics, and `Set<Integer>` for vote checking. |
| **File Handling & Streams** | `ReportService` utilizing `FileWriter`, `PrintWriter`, and buffered streams to generate CSV audit logs. |
| **JDBC & Relational Mapping** | Full `aura.dao` package utilizing `Connection`, `PreparedStatement`, `ResultSet`, and transactional commits. |
| **GUI Event-Driven Programming** | Swing listeners (`ActionListener`, `ItemListener`, `DocumentListener`) bound to FlatLaf UI controls. |
| **Enumerated Types (Enums)** | Type-safe state and category representations: `Role`, `SubmissionType`, `Priority`, `SubmissionStatus`. |
