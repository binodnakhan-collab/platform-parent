package com.platform.iam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clients", schema = "identity_access_management")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private String id;

    @Column(name = "client_id", nullable = false, unique = true, length = 50)
    private String clientId;

    @Column(name = "client_id_issued_at", nullable = false)
    private Instant clientIdIssuedAt;

    @Column(name = "client_secret", nullable = false, length = 150)
    private String clientSecret;
    private Instant clientSecretExpiresAt;

    @Column(name = "client_name", nullable = false, length = 50)
    private String clientName;

    @Column(name = "client_authentication_methods", length = 1000)
    private String clientAuthenticationMethods;

    @Column(name = "authorization_grant_types", length = 1000)
    private String authorizationGrantTypes;

    @Column(name = "redirect_uris", length = 1000)
    private String redirectUris;

    @Column(name = "post_logout_redirect_uris", length = 1000)
    private String postLogoutRedirectUris;

    @Column(name = "scopes", length = 1000)
    private String scopes;

    @Column(name = "client_settings", length = 2000)
    private String clientSettings;

    @Column(name = "token_settings", length = 2000)
    private String tokenSettings;

}
