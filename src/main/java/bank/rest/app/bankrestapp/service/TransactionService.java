package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.exception.AccountNotActiveException;
import bank.rest.app.bankrestapp.exception.InsufficientFundsException;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = {AccountNotActiveException.class, InsufficientFundsException.class}
    )
    Transaction withdraw(String senderCardNumber, String recipientCardNumber, BigDecimal amount, final String description);

    List<Transaction> getAllTransactions(String accountAccountNumber, final Pageable pageable);
}
