package com.platform.shared.util;

import com.platform.shared.exception.DuplicateResourceException;
import com.platform.shared.exception.ErrorCode;
import com.platform.shared.exception.ResourceNotFoundException;

import java.util.Optional;

public final class RepositoryHelper {

    private RepositoryHelper() {
    }

    public static <T> T getOrThrow(Optional<T> optional, ErrorCode errorCode) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(errorCode));
    }

    public static void checkDuplicate(boolean exists, ErrorCode errorCode) {
        if (exists) {
            throw new DuplicateResourceException(errorCode);
        }
    }
}