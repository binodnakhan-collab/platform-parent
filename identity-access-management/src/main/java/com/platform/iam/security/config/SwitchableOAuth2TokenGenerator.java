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
        if (OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType())) {
            return refreshTokenGenerator.generate(context);
        }

        RegisteredClient registeredClient = context.getRegisteredClient();

        TokenSettings newTokenSettings = TokenSettings.builder()
                .accessTokenFormat(useJwt ? OAuth2TokenFormat.SELF_CONTAINED : OAuth2TokenFormat.REFERENCE)
                .build();

        RegisteredClient newClient = RegisteredClient.from(registeredClient)
                .tokenSettings(newTokenSettings)
                .build();

        OAuth2TokenContext newContext = new ClientOverridingTokenContext(context, newClient);

        return useJwt ? jwtGenerator.generate(newContext) : opaqueGenerator.generate(newContext);
    }

    private static final class ClientOverridingTokenContext implements OAuth2TokenContext {

        private final OAuth2TokenContext delegate;
        private final RegisteredClient overrideClient;

        ClientOverridingTokenContext(OAuth2TokenContext delegate, RegisteredClient overrideClient) {
            this.delegate = delegate;
            this.overrideClient = overrideClient;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V> V get(Object key) {
            if (RegisteredClient.class.equals(key)) {
                return (V) overrideClient;
            }
            return delegate.get(key);
        }

        @Override
        public boolean hasKey(Object key) {
            if (RegisteredClient.class.equals(key)) {
                return true;
            }
            return delegate.hasKey(key);
        }
    }
}