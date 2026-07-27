package com.platform.shared.context;

import com.platform.shared.payload.record.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class UserContextCleanupFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            CurrentUser currentUser = resolveCurrentUser(request);
            UserContext.set(currentUser);
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private CurrentUser resolveCurrentUser(HttpServletRequest request) {
        // TODO e.g. pull from JWT, header, SecurityContext, etc.
        // TODO return null if unauthenticated / system request
        return null;
    }
}