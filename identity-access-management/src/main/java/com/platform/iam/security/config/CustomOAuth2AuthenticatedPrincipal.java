package com.platform.iam.security.config;

import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2AuthenticatedPrincipal implements OAuth2AuthenticatedPrincipal {

    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomOAuth2AuthenticatedPrincipal(
            String username,
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes) {
        this.username = username;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    @Override
    @NonNull
    public String getName() {
        return username;
    }

    @Override
    @NonNull
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
