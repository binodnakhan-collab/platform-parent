package com.platform.iam.exception;

import com.platform.shared.enums.PlatformErrorCode;
import com.platform.shared.exception.ErrorCode;
import com.platform.shared.payload.response.ApiResponse;
import com.platform.shared.payload.response.ErrorData;
import com.platform.shared.payload.response.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ONE place that decides how every exception in the application gets turned into
 * an HTTP response. Controllers/services should never catch-and-format exceptions
 * themselves - they just throw, and this class maps them consistently.
 *
 * Rule of thumb for fieldErrors:
 *   - present  -> the client sent a request where specific fields were invalid
 *                 (bean validation, bind errors, bad param types, missing params)
 *   - absent   -> everything else (not found, forbidden, conflicts, internal errors, etc.)
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseBuilder.error(
                HttpStatus.FORBIDDEN,
                PlatformErrorCode.FORBIDDEN.name(),
                PlatformErrorCode.FORBIDDEN.getMessage(),
                "You do not have permission to access this resource");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseBuilder.error(
                HttpStatus.UNAUTHORIZED,
                PlatformErrorCode.UNAUTHORIZED.name(),
                PlatformErrorCode.UNAUTHORIZED.getMessage(),
                "Invalid username or password");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleAuthentication(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseBuilder.error(
                HttpStatus.UNAUTHORIZED,
                PlatformErrorCode.UNAUTHORIZED.name(),
                PlatformErrorCode.UNAUTHORIZED.getMessage(),
                "Authentication is required to access this resource");
    }

}
