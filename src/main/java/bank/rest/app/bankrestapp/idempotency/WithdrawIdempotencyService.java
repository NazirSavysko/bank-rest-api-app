package bank.rest.app.bankrestapp.idempotency;

import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.WithdrawIdempotencyRecord;
import bank.rest.app.bankrestapp.resository.WithdrawIdempotencyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WithdrawIdempotencyService {

    private static final String WITHDRAW_ENDPOINT = "/api/v1/transactions/withdraw";
    private final WithdrawIdempotencyRepository withdrawIdempotencyRepository;
    private final ObjectMapper objectMapper;

    @Value("${idempotency.withdraw.ttl:PT24H}")
    private String withdrawTtl;

    @Transactional
    public void cacheSuccessfulResponse(final String idempotencyKey, final GetTransactionDTO response) {
        if (idempotencyKey == null || idempotencyKey.isBlank() || response == null) {
            return;
        }
        final String normalizedKey = idempotencyKey.trim();
        final String responseBody = this.serialize(response);
        final WithdrawIdempotencyRecord record = this.withdrawIdempotencyRepository
                .findByIdempotencyKeyAndEndpoint(normalizedKey, WITHDRAW_ENDPOINT)
                .orElseGet(WithdrawIdempotencyRecord::new);

        record.setIdempotencyKey(normalizedKey);
        record.setEndpoint(WITHDRAW_ENDPOINT);
        record.setResponseBody(responseBody);
        record.setExpiresAt(LocalDateTime.now().plus(Duration.parse(this.withdrawTtl)));

        this.withdrawIdempotencyRepository.save(record);
    }

    @Transactional
    public Optional<String> getCachedResponseBody(final String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }
        final String normalizedKey = idempotencyKey.trim();
        return this.withdrawIdempotencyRepository
                .findByIdempotencyKeyAndEndpoint(normalizedKey, WITHDRAW_ENDPOINT)
                .flatMap(record -> {
                    if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
                        this.withdrawIdempotencyRepository.delete(record);
                        return Optional.empty();
                    }
                    return Optional.ofNullable(record.getResponseBody());
                });
    }

    private String serialize(final GetTransactionDTO response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize idempotent response", e);
        }
    }
}
