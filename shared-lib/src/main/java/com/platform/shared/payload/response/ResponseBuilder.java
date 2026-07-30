package com.platform.shared.payload.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.platform.shared.config.TraceIdProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
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

    public static void sendError(HttpStatus status,
                                 String errorCode,
                                 String errorDescription,
                                 String message) {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null || attributes.getResponse() == null) {
            log.warn("HttpServletResponse not available.");
            return;
        }

        HttpServletResponse response = attributes.getResponse();

        ErrorData errorData = new ErrorData(
                errorCode,
                errorDescription,
                null
        );

        ApiResponse<ErrorData> apiResponse = ApiResponse.of(
                false,
                message,
                TraceIdProvider.getTraceId(),
                errorData
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_EMPTY);
        mapper.registerModule(new JavaTimeModule());

        try {
            response.setStatus(status.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), apiResponse);
            response.flushBuffer();
        } catch (IOException e) {
            log.error("Failed to write error response", e);
        }
    }
}
