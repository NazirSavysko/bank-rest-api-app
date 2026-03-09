package bank.rest.app.bankrestapp.constants;

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
    private MessageError() {
    }

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

    /**
     * Error message when a customer's password is already in use.
     */
    public static final String ERRORS_PASSWORD_ALREADY_EXISTS = "Пароль вже використовується";

    /**
     * Error message when a customer's phone number already exists.
     */
    public static final String ERRORS_PHONE_NUMBER_ALREADY_EXISTS = "Номер телефону вже використовується";

    /**
     * Error message when a customer's old password is invalid.
     */
    public static final String ERRORS_INVALID_OLD_PASSWORD = "Некоректний старий пароль";

    /**
     * Error message when a customer's new password is invalid.
     */
    public static final String ERRORS_INVALID_NEW_PASSWORD = "Некоректний новий пароль";

    /**
     * Error message when a customer with the specified email cannot be found.
     */
    public static final String ERRORS_CUSTOMER_NOT_FOUND_BY_EMAIL = "Клієнта з вказаною електронною поштою не знайдено";

    /**
     * Error message when a customer reaches the maximum allowed number of accounts.
     */
    public static final String ERRORS_MAXIMUM_NUMBER_OF_ACCOUNTS_REACHED = "Клієнт досяг максимально дозволеної кількості рахунків";

    /**
     * Error message when a customer already has an account in the requested currency.
     */
    public static final String ERRORS_ACCOUNT_WITH_CURRENCY_ALREADY_EXISTS = "У клієнта вже є рахунок у вказаній валюті";

    /**
     * Generic error message when an account cannot be found.
     */
    public static final String ERRORS_ACCOUNT_NOT_FOUND = "Рахунок не знайдено";

    /**
     * Error message when an account cannot be found by its number.
     */
    public static final String ERRORS_ACCOUNT_NOT_FOUND_BY_NUMBER = "Рахунок за вказаним номером не знайдено";

    /**
     * Error message when an account cannot be found by the provided card number.
     */
    public static final String ERRORS_ACCOUNT_NOT_FOUND_BY_CARD = "Рахунок за вказаною карткою не знайдено";

    /**
     * Error message when the account does not belong to the authenticated user.
     */
    public static final String ERRORS_ACCOUNT_OWNERSHIP_MISMATCH = "Рахунок не належить автентифікованому користувачу";

    /**
     * Error message when only UAH accounts can be used for a payment.
     */
    public static final String ERRORS_PAYMENTS_ALLOWED_ONLY_FROM_UAH_ACCOUNTS = "Платежі дозволені лише з рахунків у гривні";

    /**
     * Error message when account currency is unsupported for an IBAN payment.
     */
    public static final String ERRORS_UNSUPPORTED_ACCOUNT_CURRENCY_FOR_IBAN_PAYMENT =
            "Валюта рахунку не підтримується для платежу за IBAN";

    /**
     * Error message when the recipient IBAN is invalid.
     */
    public static final String ERRORS_INVALID_RECIPIENT_IBAN =
            "IBAN отримувача має починатися з UA та містити лише цифри після префікса";

    /**
     * Error message when a FOP account has no EDRPOU.
     */
    public static final String ERRORS_FOP_ACCOUNT_EDRPOU_REQUIRED =
            "Для рахунку ФОП обов’язково має бути вказаний код ЄДРПОУ";

    /**
     * Error message when the account balance is insufficient.
     */
    public static final String ERRORS_INSUFFICIENT_FUNDS = "Недостатньо коштів на рахунку";

    /**
     * Error message when the sender account balance is insufficient.
     */
    public static final String ERRORS_INSUFFICIENT_FUNDS_SENDER = "Недостатньо коштів на рахунку відправника";

    /**
     * Error message when an account is not active.
     */
    public static final String ERRORS_ACCOUNT_NOT_ACTIVE = "Рахунок не активний";

    /**
     * Error message when required analytics parameters are missing.
     */
    public static final String ERRORS_REQUIRED_ANALYTICS_PARAMETERS =
            "Необхідно вказати номер рахунку, рік та місяць";

    /**
     * Prefix for validation failure messages.
     */
    public static final String ERRORS_VALIDATION_FAILED_PREFIX = "Помилка валідації: ";

    /**
     * Pattern for field access failure messages.
     */
    public static final String ERRORS_FIELD_ACCESS_FAILED = "Не вдалося отримати доступ до поля: %s";

    /**
     * Pattern for exchange-rate lookup errors.
     */
    public static final String ERRORS_EXCHANGE_RATE_NOT_FOUND = "Курс для валюти %s не знайдено";
}
