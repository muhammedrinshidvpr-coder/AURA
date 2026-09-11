package aura.util;

import aura.model.User;

/** Thread-safe process-local session for the active desktop user. */
public final class SessionContext {
    private static volatile User currentUser;

    private SessionContext() {
    }

    public static void setCurrentUser(User user) {
        if (user == null) throw new IllegalArgumentException("Authenticated user cannot be null.");
        currentUser = user;
    }

    public static User getCurrentUser() { return currentUser; }
    public static boolean isAuthenticated() { return currentUser != null; }
    public static boolean hasRole(String role) { return currentUser != null && role != null && role.equalsIgnoreCase(currentUser.getRole()); }
    public static boolean isAdmin() { return hasRole("ADMIN"); }
    public static void clearSession() { currentUser = null; }
}
