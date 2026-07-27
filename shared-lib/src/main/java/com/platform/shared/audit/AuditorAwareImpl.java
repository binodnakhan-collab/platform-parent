package com.platform.shared.audit;

import com.platform.shared.context.UserContext;
import com.platform.shared.payload.record.CurrentUser;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    @NonNull
    public Optional<UUID> getCurrentAuditor() {
        CurrentUser currentUser = UserContext.get();
        return Optional.ofNullable(currentUser).map(CurrentUser::uuid);
    }
}