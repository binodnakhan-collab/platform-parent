--liquibase formatted sql

--changeset Binod Nakhan:20260729-005-create-table-users context:dev
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'identity_access_management' AND table_name = 'users'
CREATE TABLE identity_access_management.users (
                                       id          BIGINT       GENERATED ALWAYS AS IDENTITY,
                                       uuid        UUID         NOT NULL DEFAULT gen_random_uuid(),
                                       username    VARCHAR(100) NOT NULL,
                                       password    VARCHAR(100) NOT NULL,
                                       status      VARCHAR(10)  NOT NULL,
                                       CONSTRAINT pk_users PRIMARY KEY (id),
                                       CONSTRAINT uk_users_uuid UNIQUE (uuid),
                                       CONSTRAINT uk_users_username UNIQUE (username)
);

--rollback DROP TABLE identity_access_management.users;