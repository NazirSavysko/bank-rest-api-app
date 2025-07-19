package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.EmailDTO;
import bank.rest.app.bankrestapp.dto.VerifyCodeDTO;
import bank.rest.app.bankrestapp.facade.EmailFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/api/v1/email")
public final class EmailVerificationController {

    private final EmailFacade emailFacade;

    @Autowired
    public EmailVerificationController(final EmailFacade emailFacade) {
        this.emailFacade = emailFacade;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendCode(final @RequestBody EmailDTO email) {
        emailFacade.sendVerificationCode(email);

        return ok()
                .build();
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkCode(final @RequestParam VerifyCodeDTO verifyCodeDTO) {
        final boolean valid = emailFacade.verifyCode(verifyCodeDTO);

        return valid ? ok().build() : badRequest().build();
    }
}
