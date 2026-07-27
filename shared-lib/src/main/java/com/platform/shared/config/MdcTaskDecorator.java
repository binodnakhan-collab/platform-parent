package com.platform.shared.config;

import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * Register this on your @Async executor / TaskExecutor bean:
 *
 *   @Bean
 *   public Executor taskExecutor() {
 *       ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 *       executor.setTaskDecorator(new MdcTaskDecorator());
 *       ...
 *       return executor;
 *   }
 *
 * Without this, traceId is silently lost (shows as N/A) the moment work
 * hops onto a pooled/async thread, because MDC is ThreadLocal.
 */
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}