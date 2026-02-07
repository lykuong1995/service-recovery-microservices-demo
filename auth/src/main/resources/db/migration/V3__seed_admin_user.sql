INSERT INTO users (username, password, role, enabled)
VALUES ('admin', '$2y$10$fumq725dasZWmFWEd3I2POiBFlAbNsbxw0do8ifOI7U7VSdwZzK5y', 'ADMIN', TRUE)
ON CONFLICT (username) DO NOTHING;
