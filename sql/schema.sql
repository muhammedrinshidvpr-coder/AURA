-- ============================================================================
-- AURA (Autonomous University Response and Action) — Supabase PostgreSQL Schema
-- Department of Computer Science & Engineering, TKM College of Engineering (TKMCE)
-- 
-- Compatible with: PostgreSQL 15+ / Supabase Cloud Database
-- Architecture: Decoupled Anonymity Vault with Role-Based Access Control (RBAC)
-- ============================================================================

-- 1. USERS TABLE (Students & Administrators)
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'ADMIN')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Institutional access constraint: only @tkmce.ac.in emails permitted
    CONSTRAINT chk_tkmce_domain CHECK (email LIKE '%@tkmce.ac.in')
);

-- 2. SUBMISSIONS TABLE (Anonymous Campus Issues & Suggestions)
-- CRITICAL ANONYMITY LAW: This table contains NO student_id column.
-- Even direct administrative database inspection cannot associate an issue with an author.
CREATE TABLE IF NOT EXISTS submissions (
    submission_id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('ISSUE', 'SUGGESTION')),
    category VARCHAR(50) NOT NULL CHECK (category IN (
        'IT_INFRASTRUCTURE',
        'ELECTRICAL',
        'CIVIL_MAINTENANCE',
        'ACADEMIC_LABS',
        'HOSTEL_MESS',
        'GENERAL'
    )),
    location VARCHAR(150) NOT NULL,
    priority VARCHAR(20) NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN (
        'PENDING',
        'ASSIGNED',
        'IN_PROGRESS',
        'RESOLVED',
        'REJECTED'
    )),
    photo_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. STUDENT SUBMISSION RECEIPTS (The Private Anonymity Vault)
-- Maps ownership so students can view "My Submissions" without exposing identity to admins.
CREATE TABLE IF NOT EXISTS student_submission_receipts (
    receipt_id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    submission_id INT NOT NULL REFERENCES submissions(submission_id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_receipt_student_submission UNIQUE (student_id, submission_id)
);

-- 4. SUBMISSION HYPE (Upvote System for Trending Priority)
-- Students upvote campus issues to push high-impact problems to administrative attention.
CREATE TABLE IF NOT EXISTS submission_hype (
    hype_id SERIAL PRIMARY KEY,
    submission_id INT NOT NULL REFERENCES submissions(submission_id) ON DELETE CASCADE,
    student_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hype_once_per_student UNIQUE (submission_id, student_id)
);

-- 5. RESOLUTION NOTES (Official Administrative Actions & Outcomes)
CREATE TABLE IF NOT EXISTS resolution_notes (
    note_id SERIAL PRIMARY KEY,
    submission_id INT NOT NULL REFERENCES submissions(submission_id) ON DELETE CASCADE,
    admin_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    note TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. SUBMISSION HISTORY (Auditable Lifecycle Transitions)
CREATE TABLE IF NOT EXISTS submission_history (
    history_id SERIAL PRIMARY KEY,
    submission_id INT NOT NULL REFERENCES submissions(submission_id) ON DELETE CASCADE,
    old_status VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    changed_by INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- PERFORMANCE & QUERY OPTIMIZATION INDEXES
-- ============================================================================

-- Index backing Trending calculation (Hype COUNT aggregation)
CREATE INDEX IF NOT EXISTS idx_hype_submission ON submission_hype (submission_id);

-- Index backing Recency sort
CREATE INDEX IF NOT EXISTS idx_submissions_created_at ON submissions (created_at DESC);

-- Indexes backing Administrative Filtering
CREATE INDEX IF NOT EXISTS idx_submissions_status ON submissions (status);
CREATE INDEX IF NOT EXISTS idx_submissions_category ON submissions (category);
CREATE INDEX IF NOT EXISTS idx_submissions_priority ON submissions (priority);

-- Index backing Student "My Submissions" Vault Lookups
CREATE INDEX IF NOT EXISTS idx_receipts_student ON student_submission_receipts (student_id);

-- Indexes backing Audit & Resolution Queries
CREATE INDEX IF NOT EXISTS idx_history_submission ON submission_history (submission_id);
CREATE INDEX IF NOT EXISTS idx_resolution_submission ON resolution_notes (submission_id);
