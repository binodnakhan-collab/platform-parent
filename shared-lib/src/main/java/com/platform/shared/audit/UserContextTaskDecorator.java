package com.platform.shared.audit;

import com.platform.shared.context.UserContext;
import com.platform.shared.payload.record.CurrentUser;
import lombok.NonNull;
import org.springframework.core.task.TaskDecorator;

public class UserContextTaskDecorator implements TaskDecorator {

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        CurrentUser callerUser = UserContext.get();
        return () -> {
            try {
                UserContext.set(callerUser);
                runnable.run();
            } finally {
                UserContext.clear();
            }
        };
    }
}