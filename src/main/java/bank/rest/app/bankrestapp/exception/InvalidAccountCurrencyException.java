package bank.rest.app.bankrestapp.exception;

public class InvalidAccountCurrencyException extends RuntimeException {
    public InvalidAccountCurrencyException(final String message) {
        super(message);
    }
}
