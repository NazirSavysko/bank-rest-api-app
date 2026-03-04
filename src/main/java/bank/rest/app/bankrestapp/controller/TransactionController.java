package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import bank.rest.app.bankrestapp.idempotency.WithdrawIdempotencyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;

@RestController
@RequestMapping("/api/v1/transactions")
@AllArgsConstructor
class TransactionController {

    private final TransactionFacade transactionFacade;
    private final WithdrawIdempotencyService withdrawIdempotencyService;

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(final @Valid @RequestBody CreateTransaction transaction,
                                      final BindingResult bindingResult,
                                      @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> validationErrors = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> Map.of(
                            "field", fieldError.getField(),
                            "message", fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage()
                    ))
                    .toList();
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "timestamp", now(),
                            "error", "Validation Failed",
                            "messages", validationErrors
                    ));
        }

        final GetTransactionDTO getTransactionDTO = this.transactionFacade.withdraw(transaction, bindingResult);
        this.withdrawIdempotencyService.cacheSuccessfulResponse(idempotencyKey, getTransactionDTO);

        return ResponseEntity.ok(getTransactionDTO);
    }

    @GetMapping("transactions")
    public Page<GetTransactionDTO> getAllTransactions(@PageableDefault  org.springframework.data.domain.Pageable pageable, @RequestParam String accountNumber) {
        return this.transactionFacade.getAllTransactions(pageable,accountNumber);
    }


}
