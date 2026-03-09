package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.entity.enums.TransactionStatus;
import bank.rest.app.bankrestapp.exception.AccountNotActiveException;
import bank.rest.app.bankrestapp.exception.InsufficientFundsException;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import bank.rest.app.bankrestapp.service.EmailService;
import bank.rest.app.bankrestapp.service.TransactionService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_NOT_ACTIVE;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_ACCOUNT_NOT_FOUND_BY_CARD;
import static bank.rest.app.bankrestapp.constants.MessageError.ERRORS_INSUFFICIENT_FUNDS_SENDER;
import static bank.rest.app.bankrestapp.entity.enums.AccountStatus.ACTIVE;
import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.CANCELLED;
import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.COMPLETED;
import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.FAILED;
import static bank.rest.app.bankrestapp.entity.enums.TransactionType.TRANSFER;
import static java.time.LocalDateTime.now;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyLoader currencyLoader;
    private final EmailService emailService;

    @Override
    @Transactional(
            rollbackFor = Exception.class,
            noRollbackFor = {AccountNotActiveException.class, InsufficientFundsException.class}
    )
    public Transaction withdraw(final String senderCardNumber,
                                final String recipientCardNumber,
                                final BigDecimal amount,
                                final String description) {
        final LockedAccounts lockedAccounts = this.lockAccountsForTransfer(senderCardNumber, recipientCardNumber);
        final Account senderAccount = lockedAccounts.senderAccount();
        final Account recipientAccount = lockedAccounts.recipientAccount();

        this.validateActiveSenderAccount(senderAccount, recipientAccount, amount, description);
        this.validateVerifiedSender(senderAccount.getCustomer());
        this.validateSufficientFunds(senderAccount, recipientAccount, amount, description);

        final BigDecimal amountToReceive = this.resolveRecipientAmount(senderAccount, recipientAccount, amount);
        this.transferBalances(senderAccount, recipientAccount, amount, amountToReceive);

        return this.createTransaction(senderAccount, recipientAccount, amount, description, COMPLETED);
    }

    @Override
    public Page<Transaction> getAllTransactions(final String accountAccountNumber,
                                                final Account account,
                                                final Pageable pageable) {
        return this.transactionRepository.findAllTransactions(accountAccountNumber, List.of(CANCELLED, FAILED), pageable);
    }

    private void validateActiveSenderAccount(final Account senderAccount,
                                             final Account recipientAccount,
                                             final BigDecimal amount,
                                             final String description) {
        if (!ACTIVE.equals(senderAccount.getStatus())) {
            this.createTransaction(senderAccount, recipientAccount, amount, description, CANCELLED);
            throw new AccountNotActiveException(ERRORS_ACCOUNT_NOT_ACTIVE);
        }
    }

    private void validateVerifiedSender(final Customer senderCustomer) {
        this.emailService.checkIfCodeIsVerified(senderCustomer.getAuthUser().getEmail());
    }

    private void validateSufficientFunds(final Account senderAccount,
                                         final Account recipientAccount,
                                         final BigDecimal amount,
                                         final String description) {
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            this.createTransaction(senderAccount, recipientAccount, amount, description, FAILED);
            throw new InsufficientFundsException(ERRORS_INSUFFICIENT_FUNDS_SENDER);
        }
    }

    private BigDecimal resolveRecipientAmount(final Account senderAccount,
                                              final Account recipientAccount,
                                              final BigDecimal amount) {
        final Currency senderCurrency = senderAccount.getCurrencyCode();
        final Currency recipientCurrency = recipientAccount.getCurrencyCode();

        if (senderCurrency.equals(recipientCurrency)) {
            return amount;
        }

        return this.currencyLoader.convert(amount, senderCurrency.name(), recipientCurrency.name());
    }

    private void transferBalances(final Account senderAccount,
                                  final Account recipientAccount,
                                  final BigDecimal amount,
                                  final BigDecimal amountToReceive) {
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amountToReceive));
    }

    private Account getAccountByCardNumberForUpdate(final String card) {
        return this.accountRepository.findByCard_CardNumberForUpdate(card)
                .orElseThrow(() -> new NoSuchElementException(ERRORS_ACCOUNT_NOT_FOUND_BY_CARD));
    }

    private LockedAccounts lockAccountsForTransfer(final String senderCardNumber, final String recipientCardNumber) {
        if (senderCardNumber.equals(recipientCardNumber)) {
            final Account account = this.getAccountByCardNumberForUpdate(senderCardNumber);
            return new LockedAccounts(account, account);
        }

        if (senderCardNumber.compareTo(recipientCardNumber) < 0) {
            return new LockedAccounts(
                    this.getAccountByCardNumberForUpdate(senderCardNumber),
                    this.getAccountByCardNumberForUpdate(recipientCardNumber)
            );
        }

        final Account recipientAccount = this.getAccountByCardNumberForUpdate(recipientCardNumber);
        final Account senderAccount = this.getAccountByCardNumberForUpdate(senderCardNumber);
        return new LockedAccounts(senderAccount, recipientAccount);
    }

    private @NotNull Transaction createTransaction(final @NotNull Account senderAccount,
                                                   final Account recipientAccount,
                                                   final BigDecimal amount,
                                                   final String description,
                                                   final TransactionStatus status) {
        final Transaction transaction = Transaction.builder()
                .description(description)
                .amount(amount)
                .account(senderAccount)
                .toAccount(recipientAccount)
                .transactionDate(now())
                .currencyCode(senderAccount.getCurrencyCode())
                .status(status)
                .transactionType(TRANSFER)
                .build();

        senderAccount.getSentTransactions().add(transaction);
        if (recipientAccount != null) {
            recipientAccount.getReceivedTransactions().add(transaction);
        }

        return this.transactionRepository.save(transaction);
    }

    private record LockedAccounts(Account senderAccount, Account recipientAccount) {
    }
}
