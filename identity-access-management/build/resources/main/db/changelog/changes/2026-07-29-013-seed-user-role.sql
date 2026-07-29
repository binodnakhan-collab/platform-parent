--liquibase formatted sql

--changeset Binod Nakhan:20260729-013-seed-super-admin-user-role context:dev
--comment: Assign the Super Admin role to the initial admin user
--preConditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM identity_access_management.user_role WHERE user_id = 1 AND role_id = 1
INSERT INTO identity_access_management.user_role (user_id, role_id)
VALUES (1, 1);
--rollback DELETE FROM identity_access_management.user_role WHERE user_id = 1 AND role_id = 1;