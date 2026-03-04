package bank.rest.app.bankrestapp.idempotency;

import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final IdempotencyService idempotencyService;

    @Around("execution(* bank.rest.app.bankrestapp.controller.TransactionController.withdraw(..))")
    public Object applyIdempotency(final ProceedingJoinPoint joinPoint) throws Throwable {
        final HttpServletRequest request = currentRequest();
        if (request == null) {
            return joinPoint.proceed();
        }

        final String idempotencyKey = request.getHeader("X-Idempotency-Key");
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return joinPoint.proceed();
        }

        final Optional<GetTransactionDTO> cachedResponse = this.idempotencyService.getCachedTransaction(idempotencyKey);
        if (cachedResponse.isPresent()) {
            return ResponseEntity.ok(cachedResponse.get());
        }

        final Object response = joinPoint.proceed();

        if (response instanceof ResponseEntity<?> responseEntity && responseEntity.getStatusCode().is2xxSuccessful()) {
            final Object body = responseEntity.getBody();
            if (body instanceof GetTransactionDTO transactionDTO) {
                this.idempotencyService.storeTransaction(idempotencyKey, transactionDTO);
            }
        }

        return response;
    }

    private HttpServletRequest currentRequest() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
