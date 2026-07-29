--liquibase formatted sql

--changeset Binod Nakhan:20260729-07-create-user-role-table context:dev
--comment: Create join table linking users to roles (many-to-many)
--preConditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'identity_access_management' AND table_name = 'user_role'
CREATE TABLE IF NOT EXISTS identity_access_management.user_role (
                                                                    user_id BIGINT NOT NULL,
                                                                    role_id BIGINT NOT NULL,

                                                                    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id)
    REFERENCES identity_access_management.users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id)
    REFERENCES identity_access_management.roles (id) ON DELETE CASCADE
    );
--rollback DROP TABLE identity_access_management.user_role;

--comment: Index for inverse lookup - finding all users for a given role
--preConditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_indexes WHERE schemaname = 'identity_access_management' AND indexname = 'idx_user_role_role_id'
CREATE INDEX idx_user_role_role_id
    ON identity_access_management.user_role (role_id);
--rollback DROP INDEX identity_access_management.idx_user_role_role_id;