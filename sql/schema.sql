-- AURA database schema
-- Mirrors docs/DATABASE.md exactly. If you change one, change the other in the same commit.
-- Target: MySQL 8.x

CREATE DATABASE IF NOT EXISTS aura_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE aura_db;

-- ---------------------------------------------------------------------------
-- users
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)        NOT NULL,
    email         VARCHAR(150)        NOT NULL UNIQUE,
    password_hash VARCHAR(255)        NOT NULL,
    role          ENUM('STUDENT', 'ADMIN') NOT NULL
);

-- ---------------------------------------------------------------------------
-- submissions
-- student_id is a real FK (needed for a student's own "My Submissions" view)
-- but is treated as a service-layer secret by every admin-facing query.
-- See docs/DATABASE.md section 3 and docs/SECURITY.md section 5.
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS submissions (
    submission_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT                 NOT NULL,
    title         VARCHAR(200)        NOT NULL,
    description   TEXT                NOT NULL,
    type          ENUM('ISSUE', 'SUGGESTION') NOT NULL,
    priority      ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
    status        ENUM('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'REJECTED')
                      NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_submissions_student
        FOREIGN KEY (student_id) REFERENCES users(user_id)
);

-- ---------------------------------------------------------------------------
-- submission_history  (audit trail of status transitions)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS submission_history (
    history_id    INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT                 NOT NULL,
    old_status    VARCHAR(50)         NOT NULL,
    new_status    VARCHAR(50)         NOT NULL,
    changed_by    INT                 NOT NULL,
    changed_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_submission
        FOREIGN KEY (submission_id) REFERENCES submissions(submission_id),
    CONSTRAINT fk_history_changed_by
        FOREIGN KEY (changed_by) REFERENCES users(user_id)
);

-- ---------------------------------------------------------------------------
-- resolution_notes
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS resolution_notes (
    note_id       INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT                 NOT NULL,
    admin_id      INT                 NOT NULL,
    note          TEXT                NOT NULL,
    created_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notes_submission
        FOREIGN KEY (submission_id) REFERENCES submissions(submission_id),
    CONSTRAINT fk_notes_admin
        FOREIGN KEY (admin_id) REFERENCES users(user_id)
);

-- ---------------------------------------------------------------------------
-- submission_hype  (new table — the "hype"/upvote layer)
-- student_id here is the voter, not the poster; this identity relationship
-- is unrelated to submissions.student_id and does not weaken poster anonymity.
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS submission_hype (
    hype_id       INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT                 NOT NULL,
    student_id    INT                 NOT NULL,
    created_at    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hype_submission
        FOREIGN KEY (submission_id) REFERENCES submissions(submission_id),
    CONSTRAINT fk_hype_student
        FOREIGN KEY (student_id) REFERENCES users(user_id),
    CONSTRAINT uq_hype_once_per_student
        UNIQUE (submission_id, student_id)
);

-- Index backing the COUNT()-based trending sort (see docs/DATABASE.md section 2
-- for why hype count is computed rather than cached).
CREATE INDEX idx_hype_submission ON submission_hype (submission_id);
