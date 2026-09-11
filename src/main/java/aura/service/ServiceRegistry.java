package aura.service;

/**
 * Very small registry to provide service instances across UI classes during development.
 */
public final class ServiceRegistry {
    private static final SubmissionService submissionService = new DatabaseSubmissionService();

    private ServiceRegistry() {}

    public static SubmissionService getSubmissionService() {
        return submissionService;
    }
}
