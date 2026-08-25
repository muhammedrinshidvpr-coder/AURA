package aura.util;

import aura.model.User;

/**
 * Holds the currently logged-in user for the running client — in-memory,
 * single-user desktop session, no token/JWT machinery needed.
 */
public final class SessionContext {

    private static User currentUser;

    private SessionContext() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
