# AURA — Autonomous University Response and Action

A campus issue & suggestion platform for **TKM College of Engineering (TKMCE)**. Students report campus problems **anonymously**, other students can **hype** (upvote) a problem to push it up a trending list, and admin staff triage, assign, and resolve reports through a tracked status pipeline.

> Built as the S3 "Advanced Programming" (KTU CST205/CSL203) course project. See [`docs/`](docs/) for the full design before touching any code.

## Status

All 6 roadmap milestones are built: schema, models/enums, DAO layer, service layer, Swing UI, and a passing golden-path walkthrough (see [`docs/PRD.md`](docs/PRD.md) section 6). `mvn test` is green (45 tests) across `aura.dao`, `aura.service`, and `aura.util`. See [`docs/ROADMAP.md`](docs/ROADMAP.md) for what each milestone covers and [`AGENT.md`](AGENT.md) for the rules every coding session (past or future) must follow.

## Read this first

| Document | What it answers |
|---|---|
| [`AGENT.md`](AGENT.md) | The non-negotiable rules for anyone (human or agent) writing code in this repo |
| [`docs/PRD.md`](docs/PRD.md) | What we're building and why, who it's for |
| [`docs/TRD.md`](docs/TRD.md) | Tech stack, functional/non-functional requirements, what's explicitly out of scope |
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Layers, package map, class list, boundary rules |
| [`docs/DATABASE.md`](docs/DATABASE.md) | ER diagram, schema, the anonymity design |
| [`docs/SECURITY.md`](docs/SECURITY.md) | Auth, SQL-injection stance, anonymity guarantees and their honest limits |
| [`docs/TESTING.md`](docs/TESTING.md) | What must have a test, and when it gets written |
| [`docs/ROADMAP.md`](docs/ROADMAP.md) | Build order (milestones, not calendar weeks) |

## Prerequisites

- JDK 17
- Maven 3.9+
- MySQL 8.x running locally

## Setup

```bash
# 1. Create the dev database and a disposable test database (schema.sql is idempotent)
mysql -u root -p < sql/schema.sql
sed 's/aura_db/aura_test_db/g' sql/schema.sql | mysql -u root -p

# 2. (optional) load sample data into aura_db for manual testing
mysql -u root -p aura_db < sql/seed.sql

# 3. Copy the DB config templates and fill in your local credentials
cp src/main/resources/db.properties.example src/main/resources/db.properties
cp src/test/resources/db.properties.example src/test/resources/db.properties   # point this at aura_test_db

# 4. Run the test suite
mvn test

# 5. Run the app
mvn exec:java
```

Tests read `src/test/resources/db.properties` (Maven puts `target/test-classes` ahead of
`target/classes` on the test classpath), so `aura_db` is never touched by `mvn test`.

`db.properties` is git-ignored — never commit real database credentials.

## Project layout

```
AURA/
├── AGENT.md              # rules for coding sessions
├── README.md             # this file
├── pom.xml                # Maven build
├── docs/                  # PRD, TRD, architecture, database, security, testing, roadmap
├── sql/                   # schema.sql, seed.sql
└── src/                   # not created yet — see docs/ARCHITECTURE.md for the planned package map
```

## Team

| Member | Role | Focus |
|---|---|---|
| Muhammed Rinshid VP | Team Lead | Coordination, architecture, integration |
| Nirmal Binoy | Core Developer | Use-case modeling, class/package design |
| Mohammed Nafih | Core Developer | RBAC, decision pipeline, admin/student workflow |
| Rahandeep RD | Database & GUI Engineer | Schema design, Swing UI layouts |
| Athil Rahuman A | Documentation & Process Lead | Progress tracking, documentation, references |

Department of Computer Science & Engineering, TKM College of Engineering (TKMCE).
