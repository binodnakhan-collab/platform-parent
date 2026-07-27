package com.platform.shared.exception;

import com.platform.shared.enums.PlatformErrorCode;

public class SystemException extends BaseApplicationException {

    public SystemException(Throwable cause) {
        super(PlatformErrorCode.INTERNAL_ERROR);
        initCause(cause);
    }

}