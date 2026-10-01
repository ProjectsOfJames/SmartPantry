-- Smart Pantry Manager: schema, roles and permissions

CREATE TABLE IF NOT EXISTS pantry_items (
    id          BIGSERIAL     PRIMARY KEY,
    name        VARCHAR(50)   NOT NULL,
    quantity    NUMERIC(10,2) NOT NULL CHECK (quantity > 0),
    unit        VARCHAR(10)   NOT NULL,
    expiry_date DATE,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS recipes (
    id    BIGSERIAL    PRIMARY KEY,
    name  VARCHAR(100) NOT NULL UNIQUE,
    steps TEXT         NOT NULL          -- one step per line
);

CREATE TABLE IF NOT EXISTS recipe_ingredients (
    id        BIGSERIAL     PRIMARY KEY,
    recipe_id BIGINT        NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    name      VARCHAR(50)   NOT NULL,
    quantity  NUMERIC(10,2) NOT NULL CHECK (quantity > 0),
    unit      VARCHAR(10)   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_recipe_ingredients_recipe ON recipe_ingredients(recipe_id);

-- Roles used by PostgREST (created only if they do not exist yet)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'web_anon') THEN
        CREATE ROLE web_anon NOLOGIN;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'authenticator') THEN
        CREATE ROLE authenticator NOINHERIT LOGIN PASSWORD 'authpass';
    END IF;
END
$$;

GRANT web_anon TO authenticator;
GRANT USAGE ON SCHEMA public TO web_anon;

-- Recipes are read-only for the app; pantry items get full CRUD
GRANT SELECT ON recipes, recipe_ingredients TO web_anon;
GRANT SELECT, INSERT, UPDATE, DELETE ON pantry_items TO web_anon;
GRANT USAGE, SELECT ON SEQUENCE pantry_items_id_seq TO web_anon;