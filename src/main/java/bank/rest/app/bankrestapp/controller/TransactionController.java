package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
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

    /**
     * Performs a card-to-card transfer.
     *
     * @param transaction transfer payload
     * @param bindingResult validation result
     * @return response containing the created transaction DTO
     * @throws IllegalArgumentException if validation fails
     */
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(final @RequestBody CreateTransaction transaction,
                                      final BindingResult bindingResult) {

        final GetTransactionDTO getTransactionDTO = this.transactionFacade.withdraw(transaction, bindingResult);

        return ResponseEntity.ok(getTransactionDTO);
    }

    /**
     * Returns paged transaction history for the specified account number.
     *
     * @param pageable paging and sorting configuration
     * @param accountNumber account number whose history should be returned
     * @return page of transaction DTOs
     * @throws java.util.NoSuchElementException if the account cannot be found
     */
    @GetMapping("transactions")
    public Page<GetTransactionDTO> getAllTransactions(@PageableDefault(sort = {"transactionDate", "transactionId"}, direction = Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable,
                                                        @RequestParam String accountNumber) {
        return this.transactionFacade.getAllTransactions(pageable,accountNumber);
    }


}
