package com.platform.iam.security.provider;

import com.platform.iam.entity.Users;
import com.platform.iam.repository.UserRepository;
import com.platform.iam.security.config.CustomGrantPasswordAuthenticationToken;
import com.platform.iam.security.service.CustomUserDetailService;
import lombok.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.platform.iam.security.config.AuthorizationGrantTypePassword.GRANT_PASSWORD;

public class CustomGrantPasswordAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    private final OAuth2AuthorizationService authorizationService;
    private final CustomUserDetailService userDetailsService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public CustomGrantPasswordAuthenticationProvider(
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
            CustomUserDetailService userDetailsService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        Assert.notNull(userRepository, "userRepository cannot be null");
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        Object principal = (authentication != null) ? authentication.getPrincipal() : null;
        if (principal instanceof OAuth2ClientAuthenticationToken clientPrincipal && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    private static Authentication getAuthentication(UserDetails user, Users dbUser) {
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        CustomGrantPasswordAuthenticationToken customPasswordAuthenticationToken = (CustomGrantPasswordAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(customPasswordAuthenticationToken);

        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        if (registeredClient == null || !registeredClient.getAuthorizationGrantTypes().contains(GRANT_PASSWORD)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        String username = customPasswordAuthenticationToken.getUsername();
        String password = customPasswordAuthenticationToken.getPassword();
        UserDetails user;

        try {
            user = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT);
        }

        Users dbUser = userRepository.findByUsername(username).orElseThrow(() -> new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT));

        Authentication usernamePasswordAuthenticationToken = getAuthentication(user, dbUser);

        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(usernamePasswordAuthenticationToken)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(registeredClient.getScopes())
                .authorizationGrantType(GRANT_PASSWORD)
                .authorizationGrant(customPasswordAuthenticationToken);

        // Generate the access token
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);

        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(),
                generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(),
                tokenContext.getAuthorizedScopes());

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .attribute(Principal.class.getName(), usernamePasswordAuthenticationToken)
                .principalName(usernamePasswordAuthenticationToken.getName())
                .authorizationGrantType(GRANT_PASSWORD)
                .authorizedScopes(registeredClient.getScopes());

        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(accessToken, metadata ->
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)
                && !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {

            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);

            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the refresh token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }

            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            authorizationBuilder.refreshToken(refreshToken);
        }

        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);

        Map<String, Object> additionalParameters = Collections.emptyMap();

        if (registeredClient.getScopes().contains(OidcScopes.OPENID)) {

            OAuth2TokenContext idTokenContext = tokenContextBuilder
                    .tokenType(new OAuth2TokenType(OidcParameterNames.ID_TOKEN))
                    .build();

            OAuth2Token generatedIdToken = this.tokenGenerator.generate(idTokenContext);

            if (generatedIdToken == null) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the id_token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }

            OidcIdToken idToken = new OidcIdToken(
                    generatedIdToken.getTokenValue(),
                    generatedIdToken.getIssuedAt(),
                    generatedIdToken.getExpiresAt(),
                    ((ClaimAccessor) generatedIdToken).getClaims());

            authorization = OAuth2Authorization.from(authorization)
                    .token(idToken, metadata -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()))
                    .build();
            this.authorizationService.save(authorization);

            additionalParameters = new HashMap<>();
            additionalParameters.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());
        }

        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient,
                usernamePasswordAuthenticationToken,
                accessToken,
                refreshToken,
                additionalParameters);
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return CustomGrantPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}