--liquibase formatted sql

--changeset Binod Nakhan:20260729-004-create-table-roles context:dev
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'identity_access_management' AND table_name = 'roles'
CREATE TABLE identity_access_management.roles (
                                       id          BIGINT       GENERATED ALWAYS AS IDENTITY,
                                       uuid        UUID         NOT NULL DEFAULT gen_random_uuid(),
                                       name        VARCHAR(100),
                                       code        VARCHAR(100),
                                       description VARCHAR(150),
                                       is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
                                       CONSTRAINT pk_roles PRIMARY KEY (id),
                                       CONSTRAINT uk_roles_uuid UNIQUE (uuid)
);

--rollback DROP TABLE identity_access_management.roles;