package bank.rest.app.bankrestapp.validation;

import org.springframework.validation.BindingResult;

/**
 * Functional interface for validating Data Transfer Objects (DTOs).
 * Provides a contract for validating objects and handling validation results.
 *
 * @author Savysko Nazir
 * @version 1.0
 * @since 1.0
 */
@FunctionalInterface
public interface DtoValidator {

    /**
     * Validates the given DTO object and processes validation results.
     *
     * @param dto the object to validate, must not be null
     * @param result the binding result containing validation errors, if any
     * @throws IllegalArgumentException if validation fails
     */
    void validate(Object dto, BindingResult result);
}