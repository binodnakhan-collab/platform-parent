package com.platform.shared.exception;

import com.platform.shared.enums.PlatformErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Extend this for any exception that should carry its own errorCode + HTTP status.
 * GlobalExceptionHandler has one handler for this entire family, so you rarely
 * need to touch the handler again when you add a new business exception -
 * just extend this class (or BusinessException directly).
 */
@Getter
public abstract class BaseApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BaseApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected BaseApplicationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
