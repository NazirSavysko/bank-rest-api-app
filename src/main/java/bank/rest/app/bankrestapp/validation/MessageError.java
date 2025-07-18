package bank.rest.app.bankrestapp.validation;

/**
 * Utility class containing error message constants.
 * Used for centralized management of error texts in the application.
 *
 * @author Savysko Nazir
 * @version 1.0
 * @since 1.0
 */
public final class MessageError {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MessageError() {}

    /**
     * Error message when email already exists in the system.
     */
    public static final String ERRORS_EMAIL_ALREADY_EXISTS = "Електронна пошта вже використовується";

    /**
     * Error message for invalid email format.
     */
    public static final String ERRORS_INVALID_EMAIL = "Некоректна електронна пошта";

    /**
     * Error message when email verification code is invalid.
     */
    public static final String ERRORS_EMAIL_CODE_IS_INVALID = "Код підтвердження електронної пошти недійсний";

    /**
     * Error message for invalid password.
     */
    public static final String ERRORS_INVALID_PASSWORD = "Некоректний пароль";

    /**
     * Error message when a customer role is not found in the system.
     */
    public static final String ERRORS_CUSTOMER_ROLE_NOT_FOUND = "Роль клієнта не знайдено";

    /**
     * Error message when email verification code is expired.
     */
    public static final String ERRORS_EMAIL_CODE_IS_EXPIRED = "Код підтвердження електронної пошти прострочено";

    /**
     * Error message when email verification code is not verified.
     */
    public static final String ERRORS_EMAIL_NOT_VERIFIED = "Електронна пошта не підтверджена";
}
