package com.platform.shared.config;

import org.slf4j.MDC;

public final class TraceIdProvider {

    private TraceIdProvider() {
    }

    public static String getTraceId() {
        String traceId = MDC.get(TraceIdFilter.TRACE_ID_MDC_KEY);
        return traceId != null ? traceId : "N/A";
    }
}
