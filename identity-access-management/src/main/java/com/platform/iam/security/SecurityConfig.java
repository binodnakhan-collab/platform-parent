package com.platform.iam.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.iam.exception.CustomAuthenticationEntryPoint;
import com.platform.iam.security.converter.CustomOpaqueTokenAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ObjectMapper objectMapper;


    @Value("${oauth.introspectUri}")
    private String introspectUri;

    @Value("${oauth.clientId}")
    private String clientId;

    @Value("${oauth.secret}")
    private String secret;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http, OpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter)
            throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/actuator/health").permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2
                                        .opaqueToken(
                                                opaque ->
                                                        opaque
                                                                .introspectionUri(introspectUri)
                                                                .introspectionClientCredentials(clientId, secret)
                                                                .authenticationConverter(opaqueTokenAuthenticationConverter))
                                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper)))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(LogoutConfigurer::permitAll)
                .build();
    }

    @Bean
    public OpaqueTokenAuthenticationConverter customOpaqueTokenAuthenticationConverter() {
        return new CustomOpaqueTokenAuthenticationConverter();
    }

}
