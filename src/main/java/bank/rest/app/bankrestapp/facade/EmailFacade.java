package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;
import org.springframework.validation.BindingResult;

public interface EmailFacade {

    /**
     * Validates an email-verification request and sends a confirmation code.
     *
     * @param email email payload
     * @param bindingResult validation result
     * @throws IllegalArgumentException if validation fails
     */
    void sendVerificationCode(EmailDTO email, final BindingResult bindingResult);

    /**
     * Validates and verifies a confirmation code for an email address.
     *
     * @param verifyCodeDTO verification payload
     * @param bindingResult validation result
     * @throws IllegalArgumentException if validation fails or the code is invalid
     */
    void verifyCode(VerifyCodeDTO verifyCodeDTO, final BindingResult bindingResult);

}
