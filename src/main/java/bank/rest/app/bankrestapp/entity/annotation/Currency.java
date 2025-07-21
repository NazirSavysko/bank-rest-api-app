package bank.rest.app.bankrestapp.entity.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom annotation for validating currency codes in DTO fields.
 *
 * <p>This annotation is used to mark String fields that should contain
 * valid currency codes. The validation is performed by the DtoValidatorImpl
 * class during a DTO validation process.</p>
 *
 * <p>Currently supported currency codes:</p>
 * <ul>
 *   <li>UAH - Ukrainian Hryvnia</li>
 *   <li>USD - United States Dollar</li>
 *   <li>EUR - Euro</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>
 * public record CreateAccountDTO(
 *     &#64;Currency
 *     String accountType,
 *     String customerEmail
 * ) {}
 * </pre>
 *
 * <p>The validation will reject any value that is not one of the supported
 * currency codes, adding an error message "неправильний кол валют" to the
 * validation result.</p>
 *
 * @see bank.rest.app.bankrestapp.validation.DtoValidatorImpl#validateAndValidateCustomAnnotation
 * @see bank.rest.app.bankrestapp.dto.CreateAccountDTO
 *
 * @author Nazira Savisko
 * @since 1.0
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Currency {
}
