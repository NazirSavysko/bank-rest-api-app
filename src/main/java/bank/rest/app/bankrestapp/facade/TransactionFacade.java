package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

public interface TransactionFacade {

    /**
     * Validates a transfer request, performs the transfer, and maps the result to a DTO.
     *
     * @param transaction transfer request payload
     * @param bindingResult validation result
     * @return created transaction DTO
     * @throws IllegalArgumentException if validation fails
     */
    GetTransactionDTO withdraw(CreateTransaction transaction, final BindingResult bindingResult);

    /**
     * Loads paged transaction history for an account and maps it to DTOs.
     *
     * @param pageable paging configuration
     * @param accountNumber account number whose history should be returned
     * @return page of transaction DTOs
     * @throws java.util.NoSuchElementException if the account cannot be found
     */
    @Transactional(readOnly = true)
    Page<GetTransactionDTO> getAllTransactions(Pageable pageable, final String accountNumber);
}
