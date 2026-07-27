package com.platform.shared.context;

import com.platform.shared.payload.record.CurrentUser;

public final class UserContext {

    private static final ThreadLocal<CurrentUser> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(CurrentUser user) {
        CURRENT_USER.set(user);
    }

    public static CurrentUser get() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    public static void runWith(CurrentUser user, Runnable action) {
        set(user);
        try {
            action.run();
        } finally {
            clear();
        }
    }
}