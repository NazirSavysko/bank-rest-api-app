package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Transaction;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface TransactionService {

    @Transactional(rollbackFor = Exception.class)
    Transaction withdraw(String senderCardNumber, String recipientCardNumber, BigDecimal amount, final String description);

}
