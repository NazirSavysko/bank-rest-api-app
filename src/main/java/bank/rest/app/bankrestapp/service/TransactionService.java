package bank.rest.app.bankrestapp.service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface TransactionService {

    @Transactional(rollbackFor = Exception.class)
    void withdraw(String senderCardNumber, String recipientCardNumber, BigDecimal amount, final String description);

}
