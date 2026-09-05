-- ============================================================================
-- AURA (Autonomous University Response and Action) — Supabase Seed Data
-- Department of Computer Science & Engineering, TKM College of Engineering (TKMCE)
-- 
-- Populates realistic campus test data matching GUI mockups from presentation Slide 21
-- All passwords are BCrypt-hashed:
--   Students: password123 -> $2a$10$E.a5F0Z8YkM6VvA3V5O0beH3zN.1sLq8h1T5k4b7u6Y2W9k5Z0QeO
--   Admins:   admin123    -> $2a$10$7zV6bX9p1mN.a5gYvXo6neX8n5Osk.O7rZ9kC3Uf5B2K9Xn7mZa7W
-- ============================================================================

-- Clear existing data (safely in dependency order)
TRUNCATE TABLE submission_history, resolution_notes, submission_hype, student_submission_receipts, submissions, users RESTART IDENTITY CASCADE;

-- 1. SEED USERS
-- Notice: Institutional @tkmce.ac.in emails strictly enforced
INSERT INTO users (user_id, name, email, password_hash, role) VALUES
(1, 'Muhammed Rinshid VP', 'student1@tkmce.ac.in', '$2a$10$E.a5F0Z8YkM6VvA3V5O0beH3zN.1sLq8h1T5k4b7u6Y2W9k5Z0QeO', 'STUDENT'),
(2, 'Nirmal Binoy',        'student2@tkmce.ac.in', '$2a$10$E.a5F0Z8YkM6VvA3V5O0beH3zN.1sLq8h1T5k4b7u6Y2W9k5Z0QeO', 'STUDENT'),
(3, 'Mohammed Nafih',      'student3@tkmce.ac.in', '$2a$10$E.a5F0Z8YkM6VvA3V5O0beH3zN.1sLq8h1T5k4b7u6Y2W9k5Z0QeO', 'STUDENT'),
(4, 'Rahandeep RD',        'student4@tkmce.ac.in', '$2a$10$E.a5F0Z8YkM6VvA3V5O0beH3zN.1sLq8h1T5k4b7u6Y2W9k5Z0QeO', 'STUDENT'),
(5, 'Campus Maintenance',  'admin@tkmce.ac.in',    '$2a$10$7zV6bX9p1mN.a5gYvXo6neX8n5Osk.O7rZ9kC3Uf5B2K9Xn7mZa7W', 'ADMIN'),
(6, 'Estate Officer',      'estate@tkmce.ac.in',   '$2a$10$7zV6bX9p1mN.a5gYvXo6neX8n5Osk.O7rZ9kC3Uf5B2K9Xn7mZa7W', 'ADMIN');

-- Synchronize identity sequence
SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM users));

-- 2. SEED SUBMISSIONS (Anonymous Tickets from Slide 21 Mockup)
-- Crucial: No student_id is stored in this table. Anonymity is guaranteed.
INSERT INTO submissions (submission_id, title, description, type, category, location, priority, status, photo_url, created_at) VALUES
(104, 'Lab 3 Projector Lamp Faulty',
      'The overhead projector in CSE Lab 3 flickers continuously during lectures and shuts off every 10 minutes.',
      'ISSUE', 'IT_INFRASTRUCTURE', 'CSE Block - Lab 3', 'HIGH', 'IN_PROGRESS',
      'https://supabase.co/storage/v1/object/public/aura-attachments/lab3-projector.jpg',
      NOW() - INTERVAL '2 days'),

(103, 'Extend Library Hours for Midterms',
      'Requesting extended reading room operating hours up to 10:00 PM during mid-semester examination weeks.',
      'SUGGESTION', 'ACADEMIC_LABS', 'Central Library - 1st Floor', 'MEDIUM', 'PENDING',
      NULL,
      NOW() - INTERVAL '3 days'),

(98,  'Wi-Fi Disconnections in Block B',
      'Wi-Fi connectivity completely drops near classrooms B201 and B204 in Mechanical Block B.',
      'ISSUE', 'IT_INFRASTRUCTURE', 'Mechanical Block B - 2nd Floor', 'HIGH', 'RESOLVED',
      'https://supabase.co/storage/v1/object/public/aura-attachments/wifi-ap-b.jpg',
      NOW() - INTERVAL '5 days'),

(89,  'Add Water Coolers on 2nd Floor',
      'High foot traffic on Civil Block 2nd floor during break hours; an additional drinking water fountain is needed.',
      'SUGGESTION', 'CIVIL_MAINTENANCE', 'Civil Engineering Block - 2nd Floor', 'LOW', 'ASSIGNED',
      NULL,
      NOW() - INTERVAL '7 days'),

(85,  'Corridor Lights Flickering near LH3',
      'Two tube lights are flickering violently in the 3rd floor lecture hall corridor creating severe glare.',
      'ISSUE', 'ELECTRICAL', 'Main Block - 3rd Floor LH3 Corridor', 'MEDIUM', 'PENDING',
      NULL,
      NOW() - INTERVAL '1 day');

-- Synchronize identity sequence
SELECT setval('submissions_submission_id_seq', (SELECT MAX(submission_id) FROM submissions));

-- 3. SEED PRIVATE ANONYMITY VAULT RECEIPTS
-- Each student can see their own tickets under "My Submissions" via these isolated receipts.
INSERT INTO student_submission_receipts (student_id, submission_id, created_at) VALUES
(1, 104, NOW() - INTERVAL '2 days'), -- Student 1 owns ticket 104
(1, 98,  NOW() - INTERVAL '5 days'), -- Student 1 owns ticket 98
(2, 103, NOW() - INTERVAL '3 days'), -- Student 2 owns ticket 103
(3, 89,  NOW() - INTERVAL '7 days'), -- Student 3 owns ticket 89
(4, 85,  NOW() - INTERVAL '1 day');  -- Student 4 owns ticket 85

-- 4. SEED SUBMISSION HYPE (Upvotes for Trending Algorithm)
-- Notice UNIQUE(submission_id, student_id) prevents double-voting
INSERT INTO submission_hype (submission_id, student_id, created_at) VALUES
(104, 1, NOW() - INTERVAL '40 hours'),
(104, 2, NOW() - INTERVAL '38 hours'),
(104, 3, NOW() - INTERVAL '30 hours'),
(104, 4, NOW() - INTERVAL '20 hours'), -- Ticket 104 has 4 hypes (Top Trending)

(98, 2, NOW() - INTERVAL '4 days'),
(98, 3, NOW() - INTERVAL '4 days'),
(98, 4, NOW() - INTERVAL '3 days'),    -- Ticket 98 has 3 hypes

(103, 1, NOW() - INTERVAL '2 days'),
(103, 4, NOW() - INTERVAL '1 day'),    -- Ticket 103 has 2 hypes

(89, 2, NOW() - INTERVAL '6 days');    -- Ticket 89 has 1 hype

-- 5. SEED RESOLUTION NOTES (Slide 23 Mockup Content)
INSERT INTO resolution_notes (submission_id, admin_id, note, created_at) VALUES
(104, 5, 'Assigned to Campus Maintenance Electrical Team. Replacement lamp module has been dispatched.', NOW() - INTERVAL '1 day'),
(98,  5, 'IT Network center replaced faulty PoE switch and rebooted Aruba access point. Signal tested and verified normal.', NOW() - INTERVAL '1 day'),
(89,  6, 'Forwarded to Estate Office civil works division for procurement inspection.', NOW() - INTERVAL '4 days');

-- 6. SEED SUBMISSION HISTORY (Audit Trail of State Changes)
INSERT INTO submission_history (submission_id, old_status, new_status, changed_by, changed_at) VALUES
(104, 'PENDING',     'ASSIGNED',    5, NOW() - INTERVAL '36 hours'),
(104, 'ASSIGNED',    'IN_PROGRESS', 5, NOW() - INTERVAL '24 hours'),
(98,  'PENDING',     'ASSIGNED',    5, NOW() - INTERVAL '4 days'),
(98,  'ASSIGNED',    'IN_PROGRESS', 5, NOW() - INTERVAL '3 days'),
(98,  'IN_PROGRESS', 'RESOLVED',    5, NOW() - INTERVAL '1 day'),
(89,  'PENDING',     'ASSIGNED',    6, NOW() - INTERVAL '4 days');
