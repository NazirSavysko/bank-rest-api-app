package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/transactions")
@AllArgsConstructor
class TransactionController {

    private final TransactionFacade transactionFacade;

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(final @RequestBody CreateTransaction transaction,
                                      final BindingResult bindingResult) {

        final GetTransactionDTO getTransactionDTO = this.transactionFacade.withdraw(transaction, bindingResult);

        return ResponseEntity.ok(getTransactionDTO);
    }

    @GetMapping("transactions")
    public Page<GetTransactionDTO> getAllTransactions(@PageableDefault  org.springframework.data.domain.Pageable pageable,
                                                      @RequestParam String accountNumber) {
        return this.transactionFacade.getAllTransactions(pageable,accountNumber);
    }


}
