package bank.rest.app.bankrestapp.facade;

import bank.rest.app.bankrestapp.dto.CreateTransaction;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

public interface TransactionFacade {

    GetTransactionDTO withdraw(CreateTransaction transaction, final BindingResult bindingResult);

    @Transactional(readOnly = true)
    Page<GetTransactionDTO> getAllTransactions(Pageable pageable, final String accountNumber);
}
