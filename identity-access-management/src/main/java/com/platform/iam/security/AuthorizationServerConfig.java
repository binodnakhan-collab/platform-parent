package com.platform.iam.security;

import com.platform.iam.exception.OauthExceptionHandler;
import com.platform.iam.repository.UserRepository;
import com.platform.iam.security.converter.CustomOAuth2GrantPasswordAuthenticationConverter;
import com.platform.iam.security.provider.CustomGrantPasswordAuthenticationProvider;
import com.platform.iam.security.service.CustomUserDetailService;
import com.platform.iam.security.service.JpaOAuth2AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.provider", havingValue = "oauth")
public class AuthorizationServerConfig {

    private final OauthExceptionHandler oauthExceptionHandler;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationSecurityFilterChain(
            HttpSecurity http,
            CustomGrantPasswordAuthenticationProvider grantPasswordAuthenticationProvider) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http
            .csrf(AbstractHttpConfigurer::disable)
            .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
            .with(authorizationServerConfigurer, configurer -> configurer
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                    .accessTokenRequestConverter(new CustomOAuth2GrantPasswordAuthenticationConverter())
                    .authenticationProvider(grantPasswordAuthenticationProvider)
                    .errorResponseHandler(oauthExceptionHandler)
                )
                    .oidc(Customizer.withDefaults())
            )
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public CustomGrantPasswordAuthenticationProvider grantPasswordAuthenticationProvider(
            CustomUserDetailService userDetailsService,
            OAuth2TokenGenerator<?> tokenGenerator,
            JpaOAuth2AuthorizationService authorizationService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        return new CustomGrantPasswordAuthenticationProvider(
                authorizationService,
                tokenGenerator,
                userDetailsService,
                passwordEncoder,
                userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}