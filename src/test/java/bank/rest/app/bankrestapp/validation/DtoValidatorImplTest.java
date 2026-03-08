package bank.rest.app.bankrestapp.validation;

import bank.rest.app.bankrestapp.dto.CreateAccountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DtoValidatorImplTest {

    private DtoValidatorImpl dtoValidator;

    @BeforeEach
    void setUp() {
        final LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        this.dtoValidator = new DtoValidatorImpl(validatorFactoryBean);
    }

    @Test
    void validate_CreateAccountDtoFop_ShouldPass() {
        final CreateAccountDTO dto = new CreateAccountDTO("FOP", "XYZ", "alice@example.com");
        final BindingResult bindingResult = new BeanPropertyBindingResult(dto, "createAccountDTO");

        assertDoesNotThrow(() -> this.dtoValidator.validate(dto, bindingResult));
    }

    @Test
    void validate_CreateAccountDtoCurrentWithInvalidCurrency_ShouldThrow() {
        final CreateAccountDTO dto = new CreateAccountDTO("CURRENT", "XYZ", "alice@example.com");
        final BindingResult bindingResult = new BeanPropertyBindingResult(dto, "createAccountDTO");

        assertThrows(IllegalArgumentException.class, () -> this.dtoValidator.validate(dto, bindingResult));
    }
}
