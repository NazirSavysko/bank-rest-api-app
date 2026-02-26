package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.resository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void generateCard_Success() {
        // 1. Arrange
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(false);

        // 2. Act
        Card card = cardService.generateCard();


        assertNotNull(card);
        assertNotNull(card.getCardNumber());
        assertNotNull(card.getCvv());
        assertNotNull(card.getExpiryDate());

        verify(cardRepository, times(1)).existsByCardNumber(anyString());
    }

    @Test
    void generateCard_CollisionRetry() {
        // Arrange: First call returns true (exists), second call returns false (doesn't exist)
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(true).thenReturn(false);

        // Act
        Card card = cardService.generateCard();

        // Assert
        assertNotNull(card);
        // Should have called existsByCardNumber twice
        verify(cardRepository, times(2)).existsByCardNumber(anyString());
    }
}