package com.platform.iam.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.shared.config.TraceIdProvider;
import com.platform.shared.enums.PlatformErrorCode;
import com.platform.shared.payload.response.ApiResponse;
import com.platform.shared.payload.response.ErrorData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OauthExceptionHandler implements AuthenticationFailureHandler {

    private static final String OAUTH2_ACCESS_DENIED = "access_denied";
    private static final String CLIENT_NOT_FOUND = "client_not_found";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull AuthenticationException exception) throws IOException {

        if (exception instanceof OAuth2AuthenticationException oauthException) {

            String oauthErrorCode = oauthException.getError().getErrorCode();

            log.warn("OAuth2 authentication failure: code={}, description={}",
                    oauthErrorCode,
                    oauthException.getError().getDescription());

            if (CLIENT_NOT_FOUND.equals(oauthErrorCode)) {

                ErrorData errorData = new ErrorData(
                        PlatformErrorCode.UNAUTHORIZED.name(),
                        PlatformErrorCode.UNAUTHORIZED.getMessage(),
                        null
                );

                ApiResponse<ErrorData> apiResponse = ApiResponse.of(
                        false,
                        "OAuth2 client not found.",
                        TraceIdProvider.getTraceId(),
                        errorData
                );

                writeResponse(response, HttpStatus.UNAUTHORIZED, apiResponse);
                return;
            }
        }

        boolean accessDenied = isAccessDenied(exception);

        HttpStatus status = accessDenied
                ? HttpStatus.FORBIDDEN
                : HttpStatus.UNAUTHORIZED;

        PlatformErrorCode errorCode = accessDenied
                ? PlatformErrorCode.FORBIDDEN
                : PlatformErrorCode.UNAUTHORIZED;

        String message = accessDenied
                ? "Access was denied for the requested resource."
                : "Authentication failed. Please check your credentials and try again.";

        log.warn("Authentication failure [{}]: {}", errorCode, exception.getMessage());

        ErrorData errorData = new ErrorData(
                errorCode.name(),
                errorCode.getMessage(),
                null
        );

        ApiResponse<ErrorData> apiResponse = ApiResponse.of(
                false,
                message,
                TraceIdProvider.getTraceId(),
                errorData
        );

        writeResponse(response, status, apiResponse);
    }

    private boolean isAccessDenied(AuthenticationException exception) {
        return exception instanceof OAuth2AuthenticationException oauthException
                && OAUTH2_ACCESS_DENIED.equals(oauthException.getError().getErrorCode());
    }

    private void writeResponse(HttpServletResponse response,
                               HttpStatus status,
                               ApiResponse<ErrorData> body) throws IOException {

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }
}