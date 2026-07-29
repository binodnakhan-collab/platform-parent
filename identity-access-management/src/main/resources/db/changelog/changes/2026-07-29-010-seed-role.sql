--liquibase formatted sql

--changeset Binod Nakhan:20260729-010-seed-super-admin-role context:dev
--comment: Seed the Super Admin role
INSERT INTO roles (id, name, code, description, is_active)
    OVERRIDING SYSTEM VALUE
VALUES (1, 'Super Admin', 'SUPER_ADMIN', 'Super Admin role with all permissions', true)
    ON CONFLICT (id) DO NOTHING;
--rollback DELETE FROM roles WHERE id = 1;

--changeset Binod Nakhan:20260729-04-fix-roles-sequence
--comment: Resync roles_id_seq after manual PK insert, to avoid future PK collisions
--runOnChange:true
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
--rollback SELECT 1;