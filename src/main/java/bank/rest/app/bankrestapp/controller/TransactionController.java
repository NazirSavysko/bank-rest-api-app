package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.dto.get.TransactionHistoryItemDTO;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("transactions")
    public Page<GetTransactionDTO> getAllTransactions(@PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam String accountNumber) {
        return this.transactionFacade.getAllTransactions(pageable,accountNumber);
    }

    @GetMapping("/history")
    public Page<TransactionHistoryItemDTO> getTransactionHistory(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Integer accountId
    ) {
        return this.transactionFacade.getTransactionHistory(pageable, accountId);
    }

}
