--liquibase formatted sql

--changeset Binod Nakhan:20260729-011-seed-role-permissions context:dev
-- Super Admin Role -> Super Admin Permission
INSERT INTO identity_access_management.role_permission (role_id, permission_id)
VALUES (1, 1)
    ON CONFLICT (role_id, permission_id) DO NOTHING;

--rollback DELETE FROM identity_access_management.role_permission WHERE role_id = 1 AND permission_id = 1;