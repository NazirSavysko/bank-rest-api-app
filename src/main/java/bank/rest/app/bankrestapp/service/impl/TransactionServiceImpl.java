package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Customer;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.service.EmailService;
import bank.rest.app.bankrestapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;


@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final CurrencyLoader currencyLoader;
    private final EmailService emailService;

    @Autowired
    public TransactionServiceImpl(final AccountRepository accountRepository,
                                  final EmailService emailService,
                                  final CurrencyLoader currencyLoader) {
        this.accountRepository = accountRepository;
        this.currencyLoader = currencyLoader;
        this.emailService = emailService;
    }

    @Override
    public void withdraw(final String senderCardNumber, final String recipientCardNumber, final BigDecimal amount) {

        final Account senderAccount = getAccountByCardNumber(senderCardNumber);
        final Account recipientAccount = getAccountByCardNumber(recipientCardNumber);

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

        // Обновление балансов
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amountToReceive));

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);
    }

    private Account getAccountByCardNumber(final String card) {
        return accountRepository.findByCard_CardNumber(card)
                .orElseThrow(() -> new NoSuchElementException("Account not found for the provided card"));
    }
}
