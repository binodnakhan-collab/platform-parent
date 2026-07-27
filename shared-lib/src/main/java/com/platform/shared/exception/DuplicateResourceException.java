package com.platform.shared.exception;

public class DuplicateResourceException extends BaseApplicationException {

    public DuplicateResourceException(ErrorCode errorCode) {
        super(errorCode);
    }

}