package com.platform.shared.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ErrorData {

    private final String errorCode;
    private final String errorDescription;
    private final List<FieldErrorDetail> fieldErrors;

    public ErrorData(String errorCode, String errorDescription, List<FieldErrorDetail> fieldErrors) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.fieldErrors = fieldErrors;
    }

}
