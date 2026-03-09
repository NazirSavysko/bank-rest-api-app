package bank.rest.app.bankrestapp.service;

import bank.rest.app.bankrestapp.entity.Card;

public interface CardService {

    /**
     * Generates a new payment card entity with random identifying data.
     *
     * @return generated card entity
     */
    Card generateCard();
}
