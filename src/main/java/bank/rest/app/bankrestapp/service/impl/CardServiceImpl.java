package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.resository.CardRepository;
import bank.rest.app.bankrestapp.service.CardService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.lang.Math.random;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;

@Service
public final class CardServiceImpl implements CardService {

    private static final String CARD_NUMBER_PATTERN = "%016d";
    private static final int CARD_EXPIRY_YEARS = 5;

    private final CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public Card generateCard() {
        final String cardNumber = this.generateCardNumber();
        final String cvv = this.generateCvv();
        final LocalDateTime expiryDate = now().plusYears(CARD_EXPIRY_YEARS);
        final LocalDateTime createdAt = now();

        return Card.builder()
                .cardNumber(cardNumber)
                .cvv(cvv)
                .expiryDate(expiryDate)
                .createdAt(createdAt)
                .build();
    }

    private @NotNull String generateCardNumber() {
        String cardNumber;

        do {
            final long randomNumber = (long) (random() * 1_0000_0000_0000_0000L);
            cardNumber = format(CARD_NUMBER_PATTERN, randomNumber);
        } while (cardRepository.existsByCardNumber(cardNumber));

        return cardNumber;
    }

    private @NotNull String generateCvv() {
        return String.format("%03d", (int) (random() * 1000));
    }
}
