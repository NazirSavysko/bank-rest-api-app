package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;

import java.util.Optional;

public interface IdempotencyService {

    Optional<GetTransactionDTO> getCachedTransaction(String key);

    void storeTransaction(String key, GetTransactionDTO response);
}
