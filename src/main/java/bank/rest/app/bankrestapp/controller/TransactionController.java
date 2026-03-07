package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/history")
    public Page<GetTransactionDTO> getAllTransactions(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam String accountNumber) {
        return this.transactionFacade.getAllTransactions(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate")),
                accountNumber
        );
    }
}
