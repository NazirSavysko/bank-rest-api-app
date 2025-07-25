package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import bank.rest.app.bankrestapp.security.CustomerPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController {

    private final TransactionFacade transactionFacade;

    @Autowired
    public TransactionController(final TransactionFacade transactionFacade) {
        this.transactionFacade = transactionFacade;
    }


    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(final @RequestBody CreateTransaction transaction,
                                      final BindingResult bindingResult) {

        this.transactionFacade.withdraw(transaction, bindingResult);

        return ResponseEntity.ok().build();
    }
}
