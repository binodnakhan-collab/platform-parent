--liquibase formatted sql

--changeset Binod Nakhan:20260729-002-create-table-authorization-tokens context:dev
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'identity_access_management' AND table_name = 'authorization_tokens'
CREATE TABLE identity_access_management.authorization_tokens (
                                                      id                          VARCHAR(36)  NOT NULL,
                                                      principal_name              VARCHAR(100) NOT NULL,
                                                      registered_client_id        VARCHAR(50)  NOT NULL,
                                                      authorization_grant_type    VARCHAR(50)  NOT NULL,
                                                      authorized_scopes           TEXT         NOT NULL,
                                                      attributes                  TEXT         NOT NULL,
                                                      status                      VARCHAR(10)  NOT NULL,
                                                      access_token_value          TEXT         NOT NULL,
                                                      access_token_issued_at      TIMESTAMP    NOT NULL,
                                                      access_token_expires_at     TIMESTAMP    NOT NULL,
                                                      access_token_metadata       TEXT         NOT NULL,
                                                      refresh_token_value         TEXT         NOT NULL,
                                                      refresh_token_issued_at     TIMESTAMP    NOT NULL,
                                                      refresh_token_expires_at    TIMESTAMP    NOT NULL,
                                                      refresh_token_metadata      TEXT         NOT NULL,
                                                      CONSTRAINT pk_authorization_tokens PRIMARY KEY (id)
);

--rollback DROP TABLE identity_access_management.authorization_tokens;