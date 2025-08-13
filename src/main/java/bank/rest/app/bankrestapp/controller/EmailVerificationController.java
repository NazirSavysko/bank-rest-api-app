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

    @PostMapping("/send")
    public ResponseEntity<?> sendCode(final @RequestBody EmailDTO email,
                                      final BindingResult bindingResult) {
        emailFacade.sendVerificationCode(email, bindingResult);

        return ok()
                .build();
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkCode(final @RequestBody VerifyCodeDTO verifyCodeDTO,
                                       final BindingResult bindingResult) {
       this.emailFacade.verifyCode(verifyCodeDTO, bindingResult);

        return ok()
                .build();
    }
}
