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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Fired whenever an unauthenticated request hits a protected endpoint
 * (no/expired/invalid token, missing credentials, etc.). Emits the SAME
 * ApiResponse/ErrorData envelope as GlobalExceptionHandler and OauthExceptionHandler,
 * so an unauthenticated request looks like every other API error to the client -
 * no fieldErrors here, since this isn't a per-field validation failure.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {

        log.warn("Unauthenticated request to {} {}: {}",
                request.getMethod(), request.getRequestURI(), authException.getMessage());

        ErrorData errorData = new ErrorData(
                PlatformErrorCode.UNAUTHORIZED.name(),
                PlatformErrorCode.UNAUTHORIZED.getMessage(),
                null);

        ApiResponse<ErrorData> apiResponse = ApiResponse.of(
                false,
                "Your session has expired or you're not signed in. Please log in to continue.",
                TraceIdProvider.getTraceId(),
                errorData);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}