package com.platform.shared.config;

import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;

/**
 * Explicitly registers TraceIdFilter with:
 *  - a guaranteed early order (don't rely on @Order sort order vs auto-config)
 *  - REQUEST + ASYNC + ERROR dispatch types, so traceId survives async request
 *    handling and Spring Boot's /error re-dispatch, not just the initial REQUEST.
 * <p>
 * IMPORTANT: also add traceIdFilter via httpSecurity.addFilterBefore(traceIdFilter,
 * DisableEncodeUrlFilter.class) in your SecurityFilterChain bean - that is what
 * guarantees it runs before Spring Security's authentication filters specifically,
 * since Spring Security's internal filter order isn't affected by servlet-level
 * FilterRegistrationBean order at all (they're two separate ordering systems).
 */
@Configuration
public class TraceIdFilterConfig {

    @Bean
    public TraceIdFilter traceIdFilter() {
        return new TraceIdFilter();
    }

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration(TraceIdFilter traceIdFilter) {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>(traceIdFilter);
        registration.setEnabled(true);
        registration.setDispatcherTypes(EnumSet.of(
                DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR));
        registration.setOrder(Integer.MIN_VALUE);
        registration.addUrlPatterns("/*");
        return registration;
    }
}