package com.platform.iam.security.config;

import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.util.Assert;

public class SwitchableOAuth2TokenGenerator implements OAuth2TokenGenerator<OAuth2Token> {

    private final JwtGenerator jwtGenerator;
    private final OAuth2AccessTokenGenerator opaqueGenerator;
    private final OAuth2RefreshTokenGenerator refreshTokenGenerator;
    private final boolean useJwt;

    public SwitchableOAuth2TokenGenerator(JwtGenerator jwtGenerator,
                                          OAuth2AccessTokenGenerator opaqueGenerator,
                                          boolean useJwt) {
        Assert.notNull(jwtGenerator, "jwtGenerator cannot be null");
        Assert.notNull(opaqueGenerator, "opaqueGenerator cannot be null");
        this.jwtGenerator = jwtGenerator;
        this.opaqueGenerator = opaqueGenerator;
        this.useJwt = useJwt;
        this.refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    }

    @Override
    public OAuth2Token generate(OAuth2TokenContext context) {
        // Handle Refresh Tokens normally (they are always opaque)
        if (OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType())) {
            return refreshTokenGenerator.generate(context);
        }

        if (context instanceof DefaultOAuth2TokenContext defaultContext) {
            RegisteredClient registeredClient = defaultContext.getRegisteredClient();
            if (registeredClient != null) {

                // 1. Override the TokenSettings to force JWT or Opaque based on our YAML property
                TokenSettings newTokenSettings = TokenSettings.builder()
                        .accessTokenFormat(useJwt ? OAuth2TokenFormat.SELF_CONTAINED : OAuth2TokenFormat.REFERENCE)
                        .build();

                RegisteredClient newClient = RegisteredClient.from(registeredClient)
                        .tokenSettings(newTokenSettings)
                        .build();

                // 2. Rebuild the context manually, explicitly passing the tokenType so it isn't lost!
                DefaultOAuth2TokenContext newContext = DefaultOAuth2TokenContext.builder()
                        .registeredClient(newClient)
                        .principal(defaultContext.getPrincipal())
                        .authorizationServerContext(defaultContext.getAuthorizationServerContext())
                        .authorizedScopes(defaultContext.getAuthorizedScopes())
                        .authorizationGrantType(defaultContext.getAuthorizationGrantType())
                        .authorizationGrant(defaultContext.getAuthorizationGrant())
                        .tokenType(defaultContext.getTokenType())
                        .build();

                // 3. Delegate to the correct Spring generator
                return useJwt ? jwtGenerator.generate(newContext) : opaqueGenerator.generate(newContext);
            }
        }

        return useJwt ? jwtGenerator.generate(context) : opaqueGenerator.generate(context);
    }
}