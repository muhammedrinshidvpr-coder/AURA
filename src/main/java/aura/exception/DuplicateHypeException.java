package aura.exception;

/** A student tried to hype the same submission twice. */
public class DuplicateHypeException extends AuraException {

    public DuplicateHypeException(String message) {
        super(message);
    }
}
