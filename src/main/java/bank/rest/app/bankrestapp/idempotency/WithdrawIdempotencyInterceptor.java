package bank.rest.app.bankrestapp.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class WithdrawIdempotencyInterceptor implements HandlerInterceptor {

    private static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";
    private final WithdrawIdempotencyService withdrawIdempotencyService;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        final String idempotencyKey = request.getHeader(IDEMPOTENCY_HEADER);
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return true;
        }
        return this.withdrawIdempotencyService.getCachedResponseBody(idempotencyKey.trim())
                .map(cachedResponseBody -> {
                    try {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.getWriter().write(cachedResponseBody);
                        response.getWriter().flush();
                    } catch (Exception e) {
                        throw new IllegalStateException("Unable to return cached idempotent response", e);
                    }
                    return false;
                })
                .orElse(true);
    }
}
