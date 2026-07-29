--liquibase formatted sql

--changeset Binod Nakhan:20260729-003-create-table-permissions context:dev
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'identity_access_management' AND table_name = 'permissions'
CREATE TABLE identity_access_management.permissions (
                                             id          BIGINT       GENERATED ALWAYS AS IDENTITY,
                                             uuid        UUID         NOT NULL DEFAULT gen_random_uuid(),
                                             name        VARCHAR(100),
                                             code        VARCHAR(100),
                                             description VARCHAR(150),
                                             is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
                                             CONSTRAINT pk_permissions PRIMARY KEY (id),
                                             CONSTRAINT uk_permissions_uuid UNIQUE (uuid)
);

--rollback DROP TABLE identity_access_management.permissions;