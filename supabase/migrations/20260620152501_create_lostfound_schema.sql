/*
# Campus Lost and Found Schema

1. New Tables
- `users`: System users (students, staff, admins)
- `lost_items`: Lost item reports posted by users
- `found_items`: Found item reports posted by users
- `claim_requests`: Claim/return applications for lost items

2. Security
- RLS enabled on all tables.
- Single-tenant app: data is intentionally shared/public.
- Policies allow `anon` and `authenticated` full CRUD.
*/

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    username text NOT NULL UNIQUE,
    password text NOT NULL,
    email text NOT NULL,
    phone text,
    role text NOT NULL DEFAULT 'user',
    created_at timestamptz DEFAULT now()
);

-- Lost items table
CREATE TABLE IF NOT EXISTS lost_items (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid REFERENCES users(id) ON DELETE CASCADE,
    item_name text NOT NULL,
    description text,
    category text NOT NULL,
    location text NOT NULL,
    lost_time timestamptz DEFAULT now(),
    image_url text,
    status text NOT NULL DEFAULT 'pending',
    created_at timestamptz DEFAULT now()
);

-- Found items table
CREATE TABLE IF NOT EXISTS found_items (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid REFERENCES users(id) ON DELETE CASCADE,
    item_name text NOT NULL,
    description text,
    category text NOT NULL,
    location text NOT NULL,
    found_time timestamptz DEFAULT now(),
    image_url text,
    contact text NOT NULL,
    status text NOT NULL DEFAULT 'pending',
    created_at timestamptz DEFAULT now()
);

-- Claim requests table
CREATE TABLE IF NOT EXISTS claim_requests (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    lost_item_id uuid REFERENCES lost_items(id) ON DELETE CASCADE,
    found_item_id uuid REFERENCES found_items(id) ON DELETE CASCADE,
    user_id uuid REFERENCES users(id) ON DELETE CASCADE,
    status text NOT NULL DEFAULT 'pending',
    message text,
    created_at timestamptz DEFAULT now()
);

-- RLS policies (single-tenant, shared/public data)
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE lost_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE found_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE claim_requests ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "users_select" ON users;
CREATE POLICY "users_select" ON users FOR SELECT TO anon, authenticated USING (true);
DROP POLICY IF EXISTS "users_insert" ON users;
CREATE POLICY "users_insert" ON users FOR INSERT TO anon, authenticated WITH CHECK (true);
DROP POLICY IF EXISTS "users_update" ON users;
CREATE POLICY "users_update" ON users FOR UPDATE TO anon, authenticated USING (true) WITH CHECK (true);
DROP POLICY IF EXISTS "users_delete" ON users;
CREATE POLICY "users_delete" ON users FOR DELETE TO anon, authenticated USING (true);

DROP POLICY IF EXISTS "lost_items_select" ON lost_items;
CREATE POLICY "lost_items_select" ON lost_items FOR SELECT TO anon, authenticated USING (true);
DROP POLICY IF EXISTS "lost_items_insert" ON lost_items;
CREATE POLICY "lost_items_insert" ON lost_items FOR INSERT TO anon, authenticated WITH CHECK (true);
DROP POLICY IF EXISTS "lost_items_update" ON lost_items;
CREATE POLICY "lost_items_update" ON lost_items FOR UPDATE TO anon, authenticated USING (true) WITH CHECK (true);
DROP POLICY IF EXISTS "lost_items_delete" ON lost_items;
CREATE POLICY "lost_items_delete" ON lost_items FOR DELETE TO anon, authenticated USING (true);

DROP POLICY IF EXISTS "found_items_select" ON found_items;
CREATE POLICY "found_items_select" ON found_items FOR SELECT TO anon, authenticated USING (true);
DROP POLICY IF EXISTS "found_items_insert" ON found_items;
CREATE POLICY "found_items_insert" ON found_items FOR INSERT TO anon, authenticated WITH CHECK (true);
DROP POLICY IF EXISTS "found_items_update" ON found_items;
CREATE POLICY "found_items_update" ON found_items FOR UPDATE TO anon, authenticated USING (true) WITH CHECK (true);
DROP POLICY IF EXISTS "found_items_delete" ON found_items;
CREATE POLICY "found_items_delete" ON found_items FOR DELETE TO anon, authenticated USING (true);

DROP POLICY IF EXISTS "claim_requests_select" ON claim_requests;
CREATE POLICY "claim_requests_select" ON claim_requests FOR SELECT TO anon, authenticated USING (true);
DROP POLICY IF EXISTS "claim_requests_insert" ON claim_requests;
CREATE POLICY "claim_requests_insert" ON claim_requests FOR INSERT TO anon, authenticated WITH CHECK (true);
DROP POLICY IF EXISTS "claim_requests_update" ON claim_requests;
CREATE POLICY "claim_requests_update" ON claim_requests FOR UPDATE TO anon, authenticated USING (true) WITH CHECK (true);
DROP POLICY IF EXISTS "claim_requests_delete" ON claim_requests;
CREATE POLICY "claim_requests_delete" ON claim_requests FOR DELETE TO anon, authenticated USING (true);
