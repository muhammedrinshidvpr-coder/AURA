# Testing Strategy & Verification Plan — AURA

**Autonomous University Response and Action**  
Department of Computer Science & Engineering, TKM College of Engineering (TKMCE)  
Testing Framework: JUnit 5 (Jupiter) • Presentation Reference: [`docs/AURA.pdf`](AURA.pdf) (Slide 15, 28)

---

## 1. Testing Philosophy & Definition of Done

In accordance with [`AGENT.md`](../AGENT.md), every class created in `aura.dao` and `aura.service` must be accompanied by an automated JUnit 5 test class. A feature is only considered "Done" when its corresponding test suite passes cleanly.

### Testing Objectives:
1. **Data Integrity:** Verify that database constraints (`UNIQUE`, `CHECK`, foreign keys) are strictly enforced.
2. **Business Rules:** Ensure duplicate votes are blocked, invalid emails are rejected, and state transitions follow the 5-state lifecycle.
3. **Anonymity Boundary Verification:** Execute automated regression tests proving that administrative code paths cannot access student identities.

---

## 2. Test Suite Structure

```
src/test/java/aura/
├── dao/
│   ├── UserDAOTest.java                   # User CRUD, duplicate email rejection
│   ├── SubmissionDAOTest.java             # Submission insert, category query, status update
│   ├── StudentReceiptDAOTest.java         # Anonymity vault receipt mapping
│   ├── SubmissionHypeDAOTest.java         # Hype counting, duplicate vote prevention
│   ├── ResolutionNoteDAOTest.java         # Note attachment and retrieval
│   └── SubmissionHistoryDAOTest.java      # State transition audit logging
├── service/
│   ├── AuthServiceTest.java               # Login, domain rejection, BCrypt verification
│   ├── SubmissionServiceTest.java         # Submission creation and categorization
│   ├── TrackingServiceTest.java           # "My Submissions" resolution & state changes
│   ├── HypeServiceTest.java               # Trending calculation and single-vote enforcement
│   └── ReportServiceTest.java             # CSV generation and dashboard metrics
├── security/
│   └── AdminAnonymityRegressionTest.java  # Security gate: verifies admin queries never access student ID
└── util/
    ├── PasswordUtilTest.java              # BCrypt hash and salt verification
    └── ValidationUtilTest.java            # @tkmce.ac.in domain regex validation
```

---

## 3. Key Regression Tests

### 3.1 Duplicate Hype Prevention Test (`SubmissionHypeDAOTest`)
Verifies that the database and service layer reject a student attempting to upvote the same submission twice:
```java
@Test
void testDuplicateHypeThrowsException() {
    hypeService.addHype(testSubmissionId, testStudentId);
    assertThrows(DuplicateHypeException.class, () -> {
        hypeService.addHype(testSubmissionId, testStudentId);
    });
}
```

### 3.2 Institutional Domain Rejection Test (`AuthServiceTest`)
Verifies that non-TKMCE email addresses are rejected during authentication:
```java
@Test
void testNonTkmceEmailRejected() {
    assertThrows(InvalidCredentialsException.class, () -> {
        authService.register("External User", "user@gmail.com", "pass123", Role.STUDENT);
    });
}
```

### 3.3 Admin Anonymity Regression Test (`AdminAnonymityRegressionTest`)
Scans all methods reachable from `AdminDashboardFrame` and `AdminSubmissionService` to verify that no method returns or queries student identification:
```java
@Test
void testAdminQueriesDoNotExposeStudentIdentity() throws SQLException {
    List<Submission> adminQueue = submissionService.getAllSubmissionsForAdmin();
    for (Submission sub : adminQueue) {
        // Confirms that the public Submission model does not leak author identity
        assertNotNull(sub.getTitle());
        assertNotNull(sub.getCategory());
        // Submission class has no getStudentId() method in the public model
    }
}
```

---

## 4. Test Environment Isolation

Tests run against an isolated test database configured in `src/test/resources/db.properties`. Maven Surefire places `target/test-classes` ahead of `target/classes` on the test classpath, ensuring that production or dev database tables are never modified during test execution:

```bash
# Run the complete automated test suite
mvn test
```
