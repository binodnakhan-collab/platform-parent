--liquibase formatted sql

--changeset Binod Nakhan:20260729-001-create-table-clients context:dev
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'identity_access_management' AND table_name = 'clients'
CREATE TABLE identity_access_management.clients
(
    id                            VARCHAR(36)  NOT NULL,
    client_id                     VARCHAR(50)  NOT NULL,
    client_id_issued_at           TIMESTAMP    NOT NULL,
    client_secret                 VARCHAR(150) NOT NULL,
    client_secret_expires_at      TIMESTAMP,
    client_name                   VARCHAR(50)  NOT NULL,
    client_authentication_methods VARCHAR(1000),
    authorization_grant_types     VARCHAR(1000),
    redirect_uris                 VARCHAR(1000),
    post_logout_redirect_uris     VARCHAR(1000),
    scopes                        VARCHAR(1000),
    client_settings               VARCHAR(2000),
    token_settings                VARCHAR(2000),
    CONSTRAINT pk_clients PRIMARY KEY (id),
    CONSTRAINT uk_clients_client_id UNIQUE (client_id)
);

--rollback DROP TABLE identity_access_management.clients;