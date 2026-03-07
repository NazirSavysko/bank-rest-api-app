package bank.rest.app.bankrestapp.dto.get;

import bank.rest.app.bankrestapp.entity.enums.TransactionType;

import java.math.BigDecimal;

public record GetTransactionDTO(
        GetShortCustomerDTO sender,
        GetShortCustomerDTO receiver,
        BigDecimal amount,
        String description,
        String transactionDate,
        TransactionType transactionType,
        String currencyCode,
        String status,
        String senderCardNumber,
        Boolean isRecipient,
        String receiverCardNumber
) {}
