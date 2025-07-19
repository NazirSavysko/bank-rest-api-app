package bank.rest.app.bankrestapp.validation;

public final class Error {
    private Error() {}

    public static final String ERRORS_EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String ERRORS_INVALID_EMAIL = "Invalid email";

    public final static String ERRORS_INVALID_PASSWORD = "Invalid password";

    public final static String ERRORS_CUSTOMER_ROLE_NOT_FOUND = "Customer role not found";
}
