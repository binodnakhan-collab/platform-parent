--liquibase formatted sql

--changeset Binod Nakhan:20260729-006-create-table-users context:dev
CREATE TABLE IF NOT EXISTS identity_access_management.role_permission (
                                               role_id       BIGINT NOT NULL,
                                               permission_id BIGINT NOT NULL,

                                               CONSTRAINT pk_role_permission PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id)
    REFERENCES identity_access_management.roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id)
    REFERENCES permissions (id) ON DELETE CASCADE
    );

CREATE INDEX idx_role_permission_permission_id
    ON role_permission (permission_id);
