package aura.util;

import aura.model.User;

/**
 * Thread-safe global session context for the currently authenticated user.
 */
public final class SessionContext {
    private static volatile User currentUser;

    private SessionContext() {}

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }
}
