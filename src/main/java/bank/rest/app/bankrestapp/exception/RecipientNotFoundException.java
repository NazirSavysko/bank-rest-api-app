package bank.rest.app.bankrestapp.exception;

public class RecipientNotFoundException extends RuntimeException {
    public RecipientNotFoundException(final String message) {
        super(message);
    }
}
