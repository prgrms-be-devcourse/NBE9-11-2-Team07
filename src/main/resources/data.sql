INSERT INTO users (id, email, provider, provider_id, role, password, created_at)
VALUES (
           UUID(),
           'admin@test.com',
           'local',
           'admin',
           'ADMIN',
           '$2a$10$/OrjOSelOE0qQiXl3Z7e8.0mbiekPpD3jyeCz0qOCAH.Z1SrzAp1.',
           NOW()
       ) ON DUPLICATE KEY UPDATE email = email;