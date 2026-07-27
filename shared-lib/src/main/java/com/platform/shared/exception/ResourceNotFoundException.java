package com.platform.shared.exception;

import com.platform.shared.enums.PlatformErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseApplicationException {

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }


}
