package bank.rest.app.bankrestapp.exception;

public class UnsupportedCurrencyException extends RuntimeException {
    public UnsupportedCurrencyException(final String message) {
        super(message);
    }
}
