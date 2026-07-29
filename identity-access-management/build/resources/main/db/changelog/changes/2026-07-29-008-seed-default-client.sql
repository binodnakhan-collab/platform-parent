--liquibase formatted sql

--changeset Binod Nakhan:20260729-008-insert-default-client context:dev
INSERT INTO identity_access_management.clients (
    id,
    client_id,
    client_id_issued_at,
    client_secret,
    client_secret_expires_at,
    client_name,
    client_authentication_methods,
    authorization_grant_types,
    redirect_uris,
    post_logout_redirect_uris,
    scopes,
    client_settings,
    token_settings
) VALUES (
             'd9cd8704-a670-46bc-af5d-7695362386f4',
             'client',
             CURRENT_TIMESTAMP,
             '$2a$12$2us28pQsxxJ7cX59nQfVDu1m2fya/ncp8oUs5W8BA78APWFOzLQ6O',
             NULL,
             'abbc70f1-fb59-4b42-b1e4-c52fa0080bea',
             'client_secret_basic',
             'refresh_token,client_credentials,authorization_code,custom_password',
             'http://127.0.0.1:8080/login/oauth2/code/client',
             '',
             'read,profile',
             '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}',
             '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}'
         )
    ON CONFLICT (client_id) DO NOTHING;

--rollback DELETE FROM identity_access_management.clients WHERE client_id = 'client';