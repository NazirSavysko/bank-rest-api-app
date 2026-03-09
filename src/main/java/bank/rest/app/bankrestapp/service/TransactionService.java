package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.exception.AccountNotActiveException;
import bank.rest.app.bankrestapp.exception.InsufficientFundsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface TransactionService {

    /**
     * Transfers funds from one card to another and records the resulting transaction.
     *
     * @param senderCardNumber sender card number
     * @param recipientCardNumber recipient card number
     * @param amount transfer amount
     * @param description transaction description
     * @return persisted transaction entity
     * @throws AccountNotActiveException if the sender account is not active
     * @throws InsufficientFundsException if the sender account balance is insufficient
     * @throws java.util.NoSuchElementException if one of the accounts cannot be found by card number
     */
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = {AccountNotActiveException.class, InsufficientFundsException.class}
    )
    Transaction withdraw(String senderCardNumber, String recipientCardNumber, BigDecimal amount, final String description);

    /**
     * Loads paged transaction history for an account and normalizes amounts for display.
     *
     * @param accountAccountNumber account number whose history should be returned
     * @param account account context used for currency normalization
     * @param pageable paging configuration
     * @return page of transactions associated with the account
     */
    Page<Transaction> getAllTransactions(String accountAccountNumber, final Account account, final Pageable pageable);
}
