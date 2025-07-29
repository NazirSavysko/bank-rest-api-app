package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.AccountStatus;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.TransactionRepository;
import bank.rest.app.bankrestapp.service.EmailService;
import bank.rest.app.bankrestapp.service.TransactionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static bank.rest.app.bankrestapp.entity.enums.TransactionStatus.COMPLETED;
import static bank.rest.app.bankrestapp.entity.enums.TransactionType.TRANSFER;
import static java.time.LocalDateTime.now;


@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyLoader currencyLoader;
    private final EmailService emailService;

    @Autowired
    public TransactionServiceImpl(final AccountRepository accountRepository,
                                  final TransactionRepository transactionRepository,
                                  final EmailService emailService,
                                  final CurrencyLoader currencyLoader) {
        this.accountRepository = accountRepository;
        this.currencyLoader = currencyLoader;
        this.emailService = emailService;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction withdraw(final String senderCardNumber, final String recipientCardNumber, final BigDecimal amount, final String description) {

        final Account senderAccount = getAccountByCardNumber(senderCardNumber);
        final Account recipientAccount = getAccountByCardNumber(recipientCardNumber);

        if (!senderAccount.getStatus().equals(AccountStatus.ACTIVE)){
            throw new IllegalArgumentException("Account is not active");
        }

        final Customer senderCustomer = senderAccount.getCustomer();

        this.emailService.checkIfCodeIsVerified(senderCustomer.getAuthUser().getEmail());

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in sender's account");
        }

        final Currency senderCurrency = senderAccount.getCurrencyCode();
        final Currency recipientCurrency = recipientAccount.getCurrencyCode();

        BigDecimal amountToReceive = amount;

        if (!senderCurrency.equals(recipientCurrency)) {
            amountToReceive = this.currencyLoader.convert(amount, senderCurrency.name(), recipientCurrency.name());
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amountToReceive));

           return   this.createTransaction(senderAccount, recipientAccount, amount, description);
    }

    private Account getAccountByCardNumber(final String card) {
        return accountRepository.findByCard_CardNumber(card)
                .orElseThrow(() -> new NoSuchElementException("Account not found for the provided card"));
    }

    private @NotNull Transaction createTransaction(final Account senderAccount, final Account recipientAccount,
                                                   final BigDecimal amount, final String description) {
        final Transaction transaction = Transaction.builder()
                .description(description)
                .amount(amount)
                .account(senderAccount)
                .toAccount(recipientAccount)
                .transactionDate(now())
                .currencyCode(senderAccount.getCurrencyCode())
                .status(COMPLETED)
                .transactionType(TRANSFER)
                .build();

        senderAccount.getSentTransactions().add(transaction);
        recipientAccount.getReceivedTransactions().add(transaction);

        return this.transactionRepository.save(transaction);
    }
}
