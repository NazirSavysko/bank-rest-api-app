package bank.rest.app.bankrestapp.idempotency;

import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.WithdrawIdempotencyRecord;
import bank.rest.app.bankrestapp.resository.WithdrawIdempotencyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawIdempotencyServiceTest {

    @Mock
    private WithdrawIdempotencyRepository withdrawIdempotencyRepository;

    @InjectMocks
    private WithdrawIdempotencyService withdrawIdempotencyService;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(withdrawIdempotencyService, "withdrawTtl", "PT24H");
    }

    @Test
    void cacheSuccessfulResponse_PersistsSerializedResponse() throws JsonProcessingException {
        GetTransactionDTO dto = new GetTransactionDTO(null, null, BigDecimal.ONE, "d", "2026-01-01T00:00:00", "TRANSFER", "USD", "COMPLETED", "1", false, "2");
        when(withdrawIdempotencyRepository.findByIdempotencyKeyAndEndpoint("key-1", "/api/v1/transactions/withdraw"))
                .thenReturn(Optional.empty());
        when(objectMapper.writeValueAsString(dto)).thenReturn("{\"status\":\"COMPLETED\"}");

        withdrawIdempotencyService.cacheSuccessfulResponse("key-1", dto);

        ArgumentCaptor<WithdrawIdempotencyRecord> captor = ArgumentCaptor.forClass(WithdrawIdempotencyRecord.class);
        verify(withdrawIdempotencyRepository).save(captor.capture());
        assertEquals("key-1", captor.getValue().getIdempotencyKey());
        assertTrue(captor.getValue().getResponseBody().contains("\"status\":\"COMPLETED\""));
    }

    @Test
    void getCachedResponseBody_WhenExpired_RemovesRecordAndReturnsEmpty() {
        WithdrawIdempotencyRecord record = WithdrawIdempotencyRecord.builder()
                .id(1)
                .idempotencyKey("key-2")
                .endpoint("/api/v1/transactions/withdraw")
                .responseBody("{\"status\":\"COMPLETED\"}")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();
        when(withdrawIdempotencyRepository.findByIdempotencyKeyAndEndpoint("key-2", "/api/v1/transactions/withdraw"))
                .thenReturn(Optional.of(record));

        Optional<String> result = withdrawIdempotencyService.getCachedResponseBody("key-2");

        assertTrue(result.isEmpty());
        verify(withdrawIdempotencyRepository).delete(record);
    }
}
