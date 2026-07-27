package com.platform.shared.exception;

import com.platform.shared.enums.PlatformErrorCode;
import com.platform.shared.payload.response.ApiResponse;
import com.platform.shared.payload.response.ErrorData;
import com.platform.shared.payload.response.FieldErrorDetail;
import com.platform.shared.payload.response.ResponseBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        List<FieldErrorDetail> fieldErrors = groupFieldErrors(ex.getBindingResult().getFieldErrors());

        log.warn("Validation failed: {}", fieldErrors);

        return ResponseBuilder.validationError(
                HttpStatus.BAD_REQUEST,
                PlatformErrorCode.BAD_REQUEST.name(),
                PlatformErrorCode.BAD_REQUEST.getMessage(),
                "Validation failed for one or more fields",
                fieldErrors);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleBindException(BindException ex) {

        List<FieldErrorDetail> fieldErrors = groupFieldErrors(ex.getFieldErrors());

        log.warn("Binding failed: {}", fieldErrors);

        return ResponseBuilder.validationError(
                HttpStatus.BAD_REQUEST,
                PlatformErrorCode.BAD_REQUEST.name(),
                PlatformErrorCode.BAD_REQUEST.getMessage(),
                "Validation failed for one or more fields",
                fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleConstraintViolation(ConstraintViolationException ex) {

        List<FieldErrorDetail> fieldErrors = ex.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(
                        v -> lastPathSegment(v.getPropertyPath().toString()),
                        LinkedHashMap::new,
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())))
                .entrySet().stream()
                .map(e -> new FieldErrorDetail(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        log.warn("Constraint violation: {}", fieldErrors);

        return ResponseBuilder.validationError(
                HttpStatus.BAD_REQUEST,
                PlatformErrorCode.BAD_REQUEST.name(),
                PlatformErrorCode.BAD_REQUEST.getMessage(),
                "Validation failed for one or more fields",
                fieldErrors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleMissingParam(
            MissingServletRequestParameterException ex) {

        FieldErrorDetail fieldError = new FieldErrorDetail(
                ex.getParameterName(),
                Collections.singletonList("Required parameter '" + ex.getParameterName() + "' is missing"));

        return ResponseBuilder.validationError(
                HttpStatus.BAD_REQUEST,
                PlatformErrorCode.BAD_REQUEST.name(),
                PlatformErrorCode.BAD_REQUEST.getMessage(),
                "Missing required request parameter",
                List.of(fieldError));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        FieldErrorDetail fieldError = new FieldErrorDetail(
                ex.getName(),
                Collections.singletonList("Value must be of type " + requiredType));

        return ResponseBuilder.validationError(
                HttpStatus.BAD_REQUEST,
                PlatformErrorCode.BAD_REQUEST.name(),
                PlatformErrorCode.BAD_REQUEST.getMessage(),
                "Invalid parameter type",
                List.of(fieldError));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        log.warn("Malformed request body: {}", ex.getMessage());
        return ResponseBuilder.error(
                HttpStatus.BAD_REQUEST,
                PlatformErrorCode.BAD_REQUEST.name(),
                PlatformErrorCode.BAD_REQUEST.getMessage(),
                "Request body is missing or malformed JSON");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex) {
        return ResponseBuilder.error(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                PlatformErrorCode.UNSUPPORTED_MEDIA_TYPE.name(),
                PlatformErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage(),
                "Content-Type " + ex.getContentType() + " is not supported");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex) {
        return ResponseBuilder.error(
                HttpStatus.METHOD_NOT_ALLOWED,
                PlatformErrorCode.METHOD_NOT_ALLOWED.name(),
                PlatformErrorCode.METHOD_NOT_ALLOWED.getMessage(),
                ex.getMethod() + " is not supported for this endpoint");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleNoHandlerFound(NoHandlerFoundException ex) {
        return ResponseBuilder.error(
                HttpStatus.NOT_FOUND,
                PlatformErrorCode.ROUTE_NOT_FOUND.name(),
                PlatformErrorCode.ROUTE_NOT_FOUND.getMessage(),
                "No endpoint found for " + ex.getHttpMethod() + " " + ex.getRequestURL());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);
        return ResponseBuilder.error(
                HttpStatus.CONFLICT,
                PlatformErrorCode.DUPLICATE_RESOURCE.name(),
                PlatformErrorCode.DUPLICATE_RESOURCE.getMessage(),
                "Request could not be completed due to a data conflict");
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseBuilder.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                PlatformErrorCode.INTERNAL_ERROR.name(),
                PlatformErrorCode.INTERNAL_ERROR.getMessage(),
                "Something went wrong. Please try again later or contact support with the traceId.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleNoResourceFound(NoResourceFoundException ex) {
        log.error("No resource found exception", ex);
        return ResponseBuilder.error(
                HttpStatus.NOT_FOUND,
                PlatformErrorCode.ROUTE_NOT_FOUND.name(),
                PlatformErrorCode.RESOURCE_NOT_FOUND.getMessage(),
                "Something went wrong. Please try again later or contact support with the traceId.");
    }


    private List<FieldErrorDetail> groupFieldErrors(List<FieldError> fieldErrors) {
        Map<String, List<String>> grouped = fieldErrors.stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                                Collectors.toList())));

        return grouped.entrySet().stream()
                .map(e -> new FieldErrorDetail(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private String lastPathSegment(String propertyPath) {
        int lastDot = propertyPath.lastIndexOf('.');
        return lastDot >= 0 ? propertyPath.substring(lastDot + 1) : propertyPath;
    }
}
