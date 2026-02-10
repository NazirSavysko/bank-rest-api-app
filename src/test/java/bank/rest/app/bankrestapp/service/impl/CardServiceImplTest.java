package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.resository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
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
        // Arrange
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(false);

        // Act
        Card card = cardService.generateCard();

        // Assert
        assertNotNull(card);
        assertNotNull(card.getCardNumber());
        assertNotNull(card.getCvv());
        assertNotNull(card.getCreatedAt());
        assertNotNull(card.getExpiryDate()); // Expiry is createdAt + years
        assertTrue(card.getExpiryDate().isAfter(card.getCreatedAt()));

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