package aura.exception;

/** Login failed, registration used a non-TKMCE email, or an email is already registered. */
public class InvalidCredentialsException extends AuraException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
