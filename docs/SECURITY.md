# Security Architecture & Threat Model — AURA

**Autonomous University Response and Action**  
Department of Computer Science & Engineering, TKM College of Engineering (TKMCE)  
Presentation Reference: [`docs/AURA.pdf`](AURA.pdf) (Slide 2, 6, 9, 10, 16)

---

## 1. Threat Model & Security Principles

AURA is engineered to guarantee data integrity, resist injection attacks, and protect student anonymity even in the event of an internal administrative audit.

### Core Security Tenets:
1. **Defense in Depth:** Security constraints are enforced at the Database level, Service level, and Presentation level.
2. **Never Trust the Client:** Hiding an administrative button in the GUI is a UX consideration, not an authorization boundary. Every service method independently validates the caller's session role.
3. **Architectural Anonymity:** Anonymity is guaranteed by physical schema separation, not by programmer promises.

---

## 2. Institutional Authentication & Password Protection

### 2.1 Domain Gating (`@tkmce.ac.in`)
Registration and login reject any email address that does not strictly match the official TKMCE institutional domain:
- **Regex Validation:** Evaluated in `aura.util.ValidationUtil`:
  ```java
  public static boolean isValidTkmceEmail(String email) {
      return email != null && email.matches("^[a-zA-Z0-9._%+-]+@tkmce\\.ac\\.in$");
  }
  ```
- **Database Constraint:** `CHECK (email LIKE '%@tkmce.ac.in')` provides an immutable safeguard against malformed inserts.

### 2.2 BCrypt Password Hashing
- Plaintext passwords never touch persistence.
- Hashing is handled using `jBCrypt` with work factor 10:
  ```java
  String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
  ```
- Passwords are never logged, printed to console, or included in error traces.

---

## 3. SQL Injection Immunity by Construction

String concatenation in SQL statements is strictly prohibited:

```java
// VIOLATION: Never permitted under any circumstance
String sql = "SELECT * FROM submissions WHERE category = '" + category + "'";

// MANDATORY: Parameterized PreparedStatement
String sql = "SELECT * FROM submissions WHERE category = ?";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, category.name());
    try (ResultSet rs = ps.executeQuery()) { ... }
}
```

Every query throughout `aura.dao` utilizes bind variables. Automated test suites scan DAO code to verify that raw `Statement` objects are never used.

---

## 4. The Decoupled Anonymity Vault: Mathematical Proof

### 4.1 The Vulnerability of Application-Level Anonymity
In traditional architectures, the `submissions` table holds a foreign key `student_id`. While the application code may omit this column when displaying tickets to administrators, any actor with database access (DBA, faculty reviewer, or compromised credentials) can execute:

$$\text{SELECT } \text{users.name}, \text{submissions.title} \text{ FROM submissions JOIN users ON submissions.student\_id = users.user\_id};$$

This completely compromises student identity.

### 4.2 The Decoupled Vault Architecture
AURA resolves this vulnerability by splitting identity into two disconnected relational spaces:

```
Space A (Public Submissions):
Submissions(submission_id, title, description, category, location, priority, status, photo_url)
==> ZERO reference to student_id.

Space B (Private Vault):
Student_Submission_Receipts(receipt_id, student_id, submission_id)
==> Access strictly restricted to the owning student.
```

### 4.3 Threat Evaluation:
1. **Direct DB Inspection by Admin:** An administrator inspecting the `submissions` table cannot determine who submitted any issue because the column does not exist.
2. **Insider Threat on Codebase:** Administrative service methods (`AdminSubmissionService`, `TrackingService.updateStatus()`) only interact with `SubmissionDAO`. They have no dependency or access to `StudentReceiptDAO`.
3. **Student Privacy Verification:** A student can verify their own ticket status because their private session provides the student ID to query their own receipts, which then joins the public ticket data by `submission_id`.

---

## 5. Role-Based Access Control (RBAC - Slide 16)

As specified in Slide 16 of [`docs/AURA.pdf`](AURA.pdf):

| Role | Permitted Actions | Prohibited Actions |
|---|---|---|
| **STUDENT** | • Submit campus issues & suggestions.<br>• Upvote (Hype) peer submissions.<br>• View own submissions via receipt vault.<br>• Read administrative resolution notes. | • Cannot access administrative queues.<br>• Cannot modify ticket statuses.<br>• Cannot add official resolution notes.<br>• Cannot export system reports. |
| **ADMIN** | • View all campus tickets.<br>• Filter by category, priority, and recency.<br>• Update status (`ASSIGNED`, `IN_PROGRESS`, `RESOLVED`).<br>• Attach official resolution notes.<br>• Export CSV compliance reports. | • Cannot view author identity for any submission.<br>• Cannot query `student_submission_receipts`.<br>• Cannot cast student hypes. |
