package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;
import bank.rest.app.bankrestapp.facade.EmailFacade;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/api/v1/email")
@AllArgsConstructor
public final class EmailVerificationController {

    private final EmailFacade emailFacade;

    /**
     * Sends a verification code to the supplied email address.
     *
     * @param email email payload
     * @param bindingResult validation result
     * @return empty success response
     * @throws IllegalArgumentException if validation fails
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendCode(final @RequestBody EmailDTO email,
                                      final BindingResult bindingResult) {
        emailFacade.sendVerificationCode(email, bindingResult);

        return ok()
                .build();
    }

    /**
     * Checks the verification code submitted by the client.
     *
     * @param verifyCodeDTO verification payload
     * @param bindingResult validation result
     * @return empty success response
     * @throws IllegalArgumentException if validation fails or the code is invalid
     */
    @PostMapping("/check")
    public ResponseEntity<?> checkCode(final @RequestBody VerifyCodeDTO verifyCodeDTO,
                                       final BindingResult bindingResult) {
       this.emailFacade.verifyCode(verifyCodeDTO, bindingResult);

        return ok()
                .build();
    }
}
