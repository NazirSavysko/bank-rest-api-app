package bank.rest.app.bankrestapp.controller;


import bank.rest.app.bankrestapp.controller.payload.CreateAccountPayload;
import bank.rest.app.bankrestapp.dto.CreateAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.TransactionHistoryItemDTO;
import bank.rest.app.bankrestapp.entity.enums.HistoryFilter;
import bank.rest.app.bankrestapp.facade.AccountFacade;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/accounts")
@AllArgsConstructor
public final class AccountController {

    private final AccountFacade accountFacade;
    private final TransactionFacade transactionFacade;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(
            final @AuthenticationPrincipal UserDetails userDetails,
            final @RequestBody CreateAccountPayload createAccountPayload,
            final BindingResult bindingResult
            ) {
        final String customerEmail = userDetails.getUsername();
        final CreateAccountDTO createAccountDTO = new CreateAccountDTO(
                createAccountPayload.accountType(),
                customerEmail
        );

        final GetAccountDTO accountDTO = this.accountFacade.createAccount(createAccountDTO, bindingResult);

        return ResponseEntity
                .status(CREATED)
                .body(accountDTO);
    }

    @GetMapping("/{accountId}/history")
    public Page<TransactionHistoryItemDTO> getAccountHistory(
            final @PathVariable Integer accountId,
            final @RequestParam(defaultValue = "ALL") HistoryFilter filter,
            final @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return this.transactionFacade.getTransactionHistory(accountId, filter, pageable);
    }
}
