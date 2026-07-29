package com.platform.iam.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.util.StringUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class TokenConfig {

    // This reads the 'app.auth.token.type' property from application.yml
    // Defaults to "opaque" if the property is missing
    @Value("${app.auth.token.type:opaque}")
    private String tokenType;

    private static UserDetails extractUserDetails(Authentication principal) {
        if (principal == null) return null;
        if (principal instanceof AbstractAuthenticationToken authToken) {
            Object p = authToken.getPrincipal();
            return (p instanceof UserDetails ud) ? ud : null;
        }
        return null;
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair kp = generateRsaKey();
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) kp.getPublic())
                .privateKey((RSAPrivateKey) kp.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            UserDetails ud = extractUserDetails(context.getPrincipal());
            if (ud == null || !StringUtils.hasText(ud.getUsername())) {
                throw new IllegalStateException("Bad UserDetails for JWT claims");
            }
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getClaims()
                        .subject(ud.getUsername())
                        .claim("username", ud.getUsername())
                        .claim("authorities", ud.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet()));
            }
            // --- ID TOKEN CUSTOMIZATION (For OIDC) ---
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                context.getClaims()
                        .subject(ud.getUsername())
                        .claim("username", ud.getUsername())
                        .claim("email", ud.getUsername()) // Add whatever user profile info you have
                        .claim("authorities", ud.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet()));
            }
        };
    }

    @Bean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> opaqueAccessTokenCustomizer() {
        return context -> {
            UserDetails ud = extractUserDetails(context.getPrincipal());
            if (ud == null || !StringUtils.hasText(ud.getUsername())) {
                throw new IllegalStateException("Bad UserDetails for opaque claims");
            }
            context.getClaims()
                    .claim("username", ud.getUsername())
                    .claim("authorities", ud.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet()));
        };
    }

    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator(
            JwtEncoder jwtEncoder,
            OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer,
            OAuth2TokenCustomizer<OAuth2TokenClaimsContext> opaqueAccessTokenCustomizer) {

        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(jwtTokenCustomizer);

        OAuth2AccessTokenGenerator opaqueGenerator = new OAuth2AccessTokenGenerator();
        opaqueGenerator.setAccessTokenCustomizer(opaqueAccessTokenCustomizer);

        boolean useJwt = "jwt".equalsIgnoreCase(tokenType);

        // Injects the YAML choice into our custom switcher
        return new SwitchableOAuth2TokenGenerator(jwtGenerator, opaqueGenerator, useJwt);
    }
}