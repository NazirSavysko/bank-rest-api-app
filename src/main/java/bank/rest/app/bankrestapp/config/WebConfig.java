package bank.rest.app.bankrestapp.config;

import bank.rest.app.bankrestapp.idempotency.WithdrawIdempotencyInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final WithdrawIdempotencyInterceptor withdrawIdempotencyInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(withdrawIdempotencyInterceptor)
                .addPathPatterns("/api/v1/transactions/withdraw");
    }
}
