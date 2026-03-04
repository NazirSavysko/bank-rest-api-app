package bank.rest.app.bankrestapp.service.impl;

import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.IdempotencyKey;
import bank.rest.app.bankrestapp.resository.IdempotencyKeyRepository;
import bank.rest.app.bankrestapp.service.IdempotencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private static final Duration TTL = Duration.ofHours(24);

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<GetTransactionDTO> getCachedTransaction(final String key) {
        return this.idempotencyKeyRepository.findById(key)
                .map(record -> {
                    if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
                        this.idempotencyKeyRepository.delete(record);
                        return null;
                    }
                    return this.toDto(record);
                })
                .filter(Objects::nonNull);
    }

    @Override
    public void storeTransaction(final String key, final GetTransactionDTO response) {
        final LocalDateTime now = LocalDateTime.now();
        final IdempotencyKey record = IdempotencyKey.builder()
                .key(key)
                .responseBody(serialize(response))
                .createdAt(now)
                .expiresAt(now.plus(TTL))
                .build();

        this.idempotencyKeyRepository.save(record);
    }

    private GetTransactionDTO toDto(final IdempotencyKey record) {
        try {
            return this.objectMapper.readValue(record.getResponseBody(), GetTransactionDTO.class);
        } catch (JsonProcessingException e) {
            this.idempotencyKeyRepository.delete(record);
            throw new IllegalStateException("Failed to read cached idempotent response", e);
        }
    }

    private String serialize(final GetTransactionDTO dto) {
        try {
            return this.objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize idempotent response", e);
        }
    }
}
