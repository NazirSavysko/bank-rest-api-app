package bank.rest.app.bankrestapp.validation;

import bank.rest.app.bankrestapp.entity.annotation.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * Implementation of the {@link DtoValidator} interface that provides validation functionality
 * for Data Transfer Objects (DTOs) using Spring's validation framework.
 *
 * <p>This class integrates with Spring's {@link Validator} to perform JSR-303/JSR-380
 * bean validation and processes validation results. When validation errors occur,
 * it aggregates all error messages and throws an {@link IllegalArgumentException}
 * with a comprehensive error message.</p>
 *
 * <p>The validator is designed to be used in conjunction with Spring's binding and
 * validation infrastructure, particularly with {@link BindingResult} objects that
 * contain validation errors from web requests.</p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * @Autowired
 * private DtoValidator dtoValidator;
 *
 * public void processDto(CreateCustomerDTO dto, BindingResult result) {
 *     dtoValidator.validate(dto, result);
 *     // Process validated DTO...
 * }
 * }</pre>
 *
 * @author Savysko Nazir
 * @version 1.0
 * @see DtoValidator
 * @see Validator
 * @see BindingResult
 * @since 1.0
 */
@Component
public final class DtoValidatorImpl implements DtoValidator {

    /**
     * Spring's validator instance used for performing JSR-303/JSR-380 validation.
     * This validator is automatically configured by Spring Boot and supports
     * all standard validation annotations like {@code @NotNull}, {@code @NotBlank},
     * {@code @Size}, {@code @Pattern}, etc.
     */
    private final Validator validator;

    /**
     * Constructs a new DtoValidatorImpl with the specified Spring validator.
     *
     * @param validator the Spring validator instance used for validation,
     *                  automatically injected by Spring's dependency injection container
     */
    @Autowired
    public DtoValidatorImpl(final Validator validator) {
        this.validator = validator;
    }

    /**
     * Validates the given DTO object using Spring's validation framework and processes
     * any validation errors found in the BindingResult.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *   <li>Executes validation on the DTO using the configured Spring validator</li>
     *   <li>Checks if the BindingResult contains any validation errors</li>
     *   <li>If errors exist, aggregates all error messages into a single string</li>
     *   <li>Throws an IllegalArgumentException with the aggregated error message</li>
     * </ol>
     *
     * <p>The error message format is: "Validation failed: error1, error2, error3"
     * where each error corresponds to a validation constraint violation.</p>
     *
     * <h3>Common validation scenarios:</h3>
     * <ul>
     *   <li>Required field validation ({@code @NotNull}, {@code @NotBlank})</li>
     *   <li>Size constraints ({@code @Size}, {@code @Length})</li>
     *   <li>Format validation ({@code @Pattern}, {@code @Email})</li>
     *   <li>Range validation ({@code @Min}, {@code @Max})</li>
     * </ul>
     *
     * @param dto    the object to validate; must not be null and should be annotated
     *               with appropriate validation constraints
     * @param result the BindingResult that will contain validation errors after
     *               the validation process; may already contain errors from previous
     *               validation steps
     * @throws IllegalArgumentException if validation fails, containing a detailed
     *                                  message with all validation errors concatenated
     * @throws NullPointerException     if dto or result parameters are null
     * @see org.springframework.validation.annotation.Validated
     * @see jakarta.validation.Valid
     * @see jakarta.validation.constraints
     */
    @Override
    public void validate(final Object dto, final BindingResult result) {
        this.validateAndValidateCustomAnnotation(dto, result);
        if (result.hasErrors()) {
            String message = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            throw new IllegalArgumentException("Validation failed: " + message);
        }
    }

    /**
     * Validates the given DTO object and checks for custom annotations like {@link Currency}.
     * This method extends the basic validation by also checking fields annotated with
     * custom annotations and validating their values.
     *
     * <p>For example, it checks if a field annotated with {@code @Currency} contains
     * a valid currency code (e.g., "USD", "EUR","UAH"). If an invalid value is found,
     * it adds an error to the BindingResult.</p>
     *
     * @param dto    the object to validate; must not be null and should be annotated
     *               with appropriate validation constraints
     * @param result the BindingResult that will contain validation errors after
     *               the validation process; may already contain errors from previous
     *               validation steps
     */
    private void validateAndValidateCustomAnnotation(final Object dto, final BindingResult result) {
        validator.validate(dto, result);
        final Field[] fields = dto.getClass().getDeclaredFields();
        stream(fields)
                .filter(field -> field.isAnnotationPresent(Currency.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        final String value = (String) field.get(dto);
                        if (value == null || !(value.equals("UAH") || value.equals("USD") || value.equals("EUR"))) {
                            result.rejectValue(field.getName(), "currency.invalid", "неправильний кол валют");
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to access field: " + field.getName(), e);
                    }
                });
    }
}
