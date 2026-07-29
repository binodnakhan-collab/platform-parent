--liquibase formatted sql

--changeset Binod Nakhan:20260729-009-insert-super-admin-permission context:dev
INSERT INTO identity_access_management.permissions (id, name, code, description, is_active)
    OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Super Admin', 'SUPER_ADMIN', 'Super Admin can access all features.', true)
    ON CONFLICT (id) DO NOTHING;

-- Advance the identity sequence to prevent future PK collisions
SELECT setval(
               pg_get_serial_sequence('identity_access_management.permissions', 'id'),
               (SELECT MAX(id) FROM identity_access_management.permissions)
       );

--rollback DELETE FROM identity_access_management.permissions WHERE id = 1;