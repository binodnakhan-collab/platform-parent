package com.platform.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "authorization_tokens", schema = "identity_access_management")
public class AuthorizationToken {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private String id;

    @Column(name = "principal_name", nullable = false, length = 100)
    private String principalName;

    @Column(name = "registered_client_id", nullable = false, length = 50)
    private String registeredClientId;

    @Column(name = "authorization_grant_type", nullable = false, length = 50)
    private String authorizationGrantType;

    @Column(name = "authorized_scopes", columnDefinition = "TEXT", nullable = false, length = 150)
    private String authorizedScopes;

    @Column(name = "attributes", columnDefinition = "TEXT", nullable = false)
    private String attributes;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "access_token_value", columnDefinition = "TEXT", nullable = false, length = 200)
    private String accessTokenValue;

    @Column(name = "access_token_issued_at", nullable = false)
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at", nullable = false)
    private Instant accessTokenExpiresAt;

    @Column(name = "access_token_metadata", columnDefinition = "TEXT", nullable = false, length = 200)
    private String accessTokenMetadata;

    @Column(name = "refresh_token_value", columnDefinition = "TEXT", nullable = false, length = 200)
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at", nullable = false)
    private Instant refreshTokenIssuedAt;

    @Column(name = "refresh_token_expires_at", nullable = false)
    private Instant refreshTokenExpiresAt;

    @Column(name = "refresh_token_metadata", columnDefinition = "TEXT", nullable = false, length = 200)
    private String refreshTokenMetadata;

}
