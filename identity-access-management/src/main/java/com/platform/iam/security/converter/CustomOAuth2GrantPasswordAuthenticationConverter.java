package com.platform.iam.security.converter;

import com.platform.iam.security.config.CustomGrantPasswordAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.platform.iam.security.config.AuthorizationGrantTypePassword.GRANT_PASSWORD;

public class CustomOAuth2GrantPasswordAuthenticationConverter implements AuthenticationConverter {

    private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
        parameterMap.forEach(
                (key, values) -> {
                    for (String value : values) {
                        parameters.add(key, value);
                    }
                });
        return parameters;
    }

    /**
     * Converts an HTTP request into an {@link Authentication} object for the OAuth2
     * grant_type=password flow.
     *
     * @param request The HTTP request to be converted.
     * @return An {@link Authentication} object representing the client and user credentials, or
     * {@code null} if the grant type is not supported.
     * @throws OAuth2AuthenticationException If the request parameters are invalid.
     */
    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!GRANT_PASSWORD.getValue().equals(grantType)) {
            return null;
        }
        MultiValueMap<String, String> parameters = getParameters(request);
        // scope (OPTIONAL)
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        // username (REQUIRED)
        String username = parameters.getFirst("username");
        if (!StringUtils.hasText(username)
                || parameters.get("username").size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        // password (REQUIRED)
        String password = parameters.getFirst("password");
        if (!StringUtils.hasText(password)
                || parameters.get("password").size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes =
                    new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }

        Map<String, Object> additionalParameters =
                parameters.entrySet().stream()
                        .filter(
                                entry ->
                                        !OAuth2ParameterNames.GRANT_TYPE.equals(entry.getKey())
                                                && !OAuth2ParameterNames.SCOPE.equals(entry.getKey())
                                                && !"password".equals(entry.getKey())
                                                && !"username".equals(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFirst()));

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

        return new CustomGrantPasswordAuthenticationToken(clientPrincipal, username, password, requestedScopes, additionalParameters);
    }
}
