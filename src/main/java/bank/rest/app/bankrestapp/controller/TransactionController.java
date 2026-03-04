package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import bank.rest.app.bankrestapp.validation.DtoValidator;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@AllArgsConstructor
class TransactionController {

    private final TransactionFacade transactionFacade;
    private final DtoValidator dtoValidator;

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(final @Valid @RequestBody CreateTransaction transaction,
                                      final BindingResult bindingResult) {

        try {
            this.dtoValidator.validate(transaction, bindingResult);
        } catch (IllegalArgumentException ignored) {
            // errors are collected in BindingResult and handled below
        }

        if (bindingResult.hasErrors()) {
            final Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (first, second) -> first, LinkedHashMap::new));

            return ResponseEntity.badRequest().body(Map.of(
                    "errors", errors
            ));
        }

        final GetTransactionDTO getTransactionDTO = this.transactionFacade.withdraw(transaction, bindingResult);

        return ResponseEntity.ok(getTransactionDTO);
    }

    @GetMapping("transactions")
    public Page<GetTransactionDTO> getAllTransactions(@PageableDefault  org.springframework.data.domain.Pageable pageable, @RequestParam String accountNumber) {
        return this.transactionFacade.getAllTransactions(pageable,accountNumber);
    }


}
