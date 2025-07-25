package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.resository.AccountRepository;
import bank.rest.app.bankrestapp.resository.CardRepository;
import bank.rest.app.bankrestapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

import static java.math.RoundingMode.HALF_UP;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;

    @Autowired
    public TransactionServiceImpl(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void withdraw(final String senderCardNumber, final String recipientCardNumber, final BigDecimal amount) {
       final Account senderAccount = getAccountByCardNumber(senderCardNumber);
        final Account recipientAccount = getAccountByCardNumber(recipientCardNumber);

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in sender's account");
        }

        final Currency senderCurrency = senderAccount.getCurrencyCode();
        final Currency recipientCurrency = recipientAccount.getCurrencyCode();

        BigDecimal amountToReceive = amount;

        if (!senderCurrency.equals(recipientCurrency)) {
            final BigDecimal senderRate = getRate(senderCurrency);
            final BigDecimal recipientRate = getRate(recipientCurrency);

            amountToReceive = amount
                    .divide(senderRate, 4, HALF_UP)
                    .multiply(recipientRate)
                    .setScale(2, HALF_UP);
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
