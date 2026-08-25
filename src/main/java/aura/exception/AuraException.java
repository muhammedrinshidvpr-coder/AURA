package aura.exception;

/** Base unchecked exception for all application-level errors in AURA. */
public class AuraException extends RuntimeException {

    public AuraException(String message) {
        super(message);
    }

    public AuraException(String message, Throwable cause) {
        super(message, cause);
    }
}
