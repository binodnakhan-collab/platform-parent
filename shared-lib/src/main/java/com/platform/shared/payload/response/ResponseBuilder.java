package com.platform.shared.payload.response;

import com.platform.shared.config.TraceIdProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.MessageFormat;
import java.util.List;

public final class ResponseBuilder {

    private ResponseBuilder() {
    }

    /**
     * Success response with message only.
     */
    public static ApiResponse<Void> success(String message) {
        return ApiResponse.of(
                true,
                message,
                TraceIdProvider.getTraceId(),
                null
        );
    }

    /**
     * Success response with data and message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.of(
                true,
                message,
                TraceIdProvider.getTraceId(),
                data
        );
    }

    /**
     * Success response with formatted message.
     * <p>
     * Example:
     * success("User {0} created successfully.", username);
     */
    public static ApiResponse<Void> success(String message, Object... params) {
        return ApiResponse.of(
                true,
                MessageFormat.format(message, params),
                TraceIdProvider.getTraceId(),
                null
        );
    }

    /**
     * Generic error - no fieldErrors. Used for almost every exception type.
     */
    public static ResponseEntity<ApiResponse<ErrorData>> error(
            HttpStatus status, String errorCode, String errorDescription, String message) {
        ErrorData errorData = new ErrorData(errorCode, errorDescription, null);
        ApiResponse<ErrorData> body = ApiResponse.of(false, message, TraceIdProvider.getTraceId(), errorData);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Only used where individual request fields failed validation (e.g. bean validation, bind errors).
     */
    public static ResponseEntity<ApiResponse<ErrorData>> validationError(
            HttpStatus status, String errorCode, String errorDescription,
            String message, List<FieldErrorDetail> fieldErrors) {
        ErrorData errorData = new ErrorData(errorCode, errorDescription, fieldErrors);
        ApiResponse<ErrorData> body = ApiResponse.of(false, message, TraceIdProvider.getTraceId(), errorData);
        return ResponseEntity.status(status).body(body);
    }
}
