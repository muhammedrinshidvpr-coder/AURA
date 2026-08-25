package aura.exception;

/** A service method's session/role check failed. */
public class UnauthorizedActionException extends AuraException {

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
