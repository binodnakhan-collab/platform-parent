package com.platform.iam.security.config;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.platform.iam.security.config.AuthorizationGrantTypePassword.GRANT_PASSWORD;

@Getter
public class CustomGrantPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final String username;
    private final String password;
    private final Set<String> scopes;

    public CustomGrantPasswordAuthenticationToken(Authentication clientPrincipal, String username, String password, Set<String> scopes,
                                                  @NonNull Map<String, Object> additionalParameters) {
        super(GRANT_PASSWORD, clientPrincipal, additionalParameters);
        Assert.hasText(username, "username cannot be empty");
        Assert.hasText(password, "password cannot be empty");
        this.username = username;
        this.password = password;
        this.scopes = scopes != null ? Set.copyOf(scopes) : Collections.emptySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomGrantPasswordAuthenticationToken that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username);
    }


}
