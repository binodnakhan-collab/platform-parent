package com.platform.shared.enums;

import com.platform.shared.exception.ErrorCode;

/**
 * Single source of truth for error codes returned to clients.
 * Keep these stable - clients/QA/support will branch on errorCode, not on the message text.
 */
public enum PlatformErrorCode implements ErrorCode {

    BAD_REQUEST("REQ001", "Invalid request"),
    VALIDATION_ERROR("VAL001", "One or more fields failed validation"),
    RESOURCE_NOT_FOUND("RES001", "Requested resource was not found"),
    ROUTE_NOT_FOUND("ROU001", "Requested endpoint does not exist"),
    UNAUTHORIZED("SEC002", "Authentication is required"),
    FORBIDDEN("SEC002", "You do not have permission to perform this action"),
    METHOD_NOT_ALLOWED("REQ002", "HTTP method not supported for this endpoint"),
    UNSUPPORTED_MEDIA_TYPE("REQ003", "Request content type is not supported"),
    DUPLICATE_RESOURCE("RES002", "Resource already exists"),
    DOWNSTREAM_ERROR("EXT001", "An upstream/downstream service call failed"),
    INTERNAL_ERROR("SYS001", "An unexpected error occurred"),
    MISSING_HEADER("HED001", "Header is required."),
    GENERIC_ERROR_MESSAGE("GEN001", "Something went wrong. Please try again later or contact support with the traceId.");

    private final String code;
    private final String message;
    PlatformErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
