--liquibase formatted sql

--changeset Binod Nakhan:20260729-012-insert-super-admin-user context:dev
INSERT INTO identity_access_management.users (id, username, password, status)
    OVERRIDING SYSTEM VALUE
VALUES (1, 'superadmin@localhost.com', '$2a$12$w0tMEu35demh3kKj8Lgs.uGIu4RnVdaWE6cUGNAbCNTbScpM8h7mW', 'ACTIVE')
    ON CONFLICT (id) DO NOTHING;

SELECT setval(
               pg_get_serial_sequence('identity_access_management.users', 'id'),
               (SELECT MAX(id) FROM identity_access_management.users)
       );

--rollback DELETE FROM identity_access_management.users WHERE id = 1;