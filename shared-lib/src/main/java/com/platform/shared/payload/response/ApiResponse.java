package com.platform.shared.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "success",
        "message",
        "timestamp",
        "traceId",
        "data"
})
@Builder
@AllArgsConstructor
public final class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final Instant timestamp;
    private final String traceId;
    private final T data;

    private ApiResponse(boolean success, String message, String traceId, T data) {
        this.success = success;
        this.message = message;
        this.timestamp = Instant.now();
        this.traceId = traceId;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(boolean success, String message, String traceId, T data) {
        return new ApiResponse<>(success, message, traceId, data);
    }



}
