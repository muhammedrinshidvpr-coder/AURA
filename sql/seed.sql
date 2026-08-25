-- AURA sample data — for manual testing only, never used in production.
-- Run after schema.sql:  mysql -u root -p aura_db < sql/seed.sql
--
-- NOTE: password_hash values below are placeholders, NOT real BCrypt hashes.
-- They exist so the seeded rows are non-empty and easy to read; they will not
-- successfully verify against any plaintext password once AuthService/PasswordUtil
-- exist. Register real accounts through the app (or a small seeding utility that
-- calls PasswordUtil) to get logins that actually work end-to-end.

USE aura_db;

INSERT INTO users (name, email, password_hash, role) VALUES
    ('Asha Menon',   'asha.menon@tkmce.ac.in',   'PLACEHOLDER_NOT_A_REAL_BCRYPT_HASH', 'STUDENT'),
    ('Rahul Nair',   'rahul.nair@tkmce.ac.in',   'PLACEHOLDER_NOT_A_REAL_BCRYPT_HASH', 'STUDENT'),
    ('Divya Pillai', 'divya.pillai@tkmce.ac.in', 'PLACEHOLDER_NOT_A_REAL_BCRYPT_HASH', 'ADMIN');

-- submission 1: posted by Asha (user_id 1), no admin action yet
INSERT INTO submissions (student_id, title, description, type, priority, status) VALUES
    (1, 'Wi-Fi disconnects in Block B', 'Wi-Fi drops every few minutes in the Block B seminar halls during lab hours.', 'ISSUE', 'HIGH', 'PENDING');

-- submission 2: posted by Rahul (user_id 2), already resolved
INSERT INTO submissions (student_id, title, description, type, priority, status) VALUES
    (2, 'Add water coolers on 2nd floor', 'The 2nd floor has no water cooler; students walk down two floors for water.', 'SUGGESTION', 'LOW', 'RESOLVED');

-- hype: Rahul hypes submission 1 (Asha's post) — voter identity is stored,
-- poster identity (student_id on submissions) is never exposed by this.
INSERT INTO submission_hype (submission_id, student_id) VALUES
    (1, 2);

-- history for submission 2's resolution, recorded by the admin (user_id 3)
INSERT INTO submission_history (submission_id, old_status, new_status, changed_by) VALUES
    (2, 'PENDING', 'ASSIGNED', 3),
    (2, 'ASSIGNED', 'IN_PROGRESS', 3),
    (2, 'IN_PROGRESS', 'RESOLVED', 3);

-- resolution note for submission 2
INSERT INTO resolution_notes (submission_id, admin_id, note) VALUES
    (2, 3, 'Facilities team installed a water cooler near the 2nd floor stairwell.');
