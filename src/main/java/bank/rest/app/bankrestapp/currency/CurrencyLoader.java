package bank.rest.app.bankrestapp.currency;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public final class CurrencyLoader {

    private final RestTemplate restTemplate;
    private List<CurrencyRate> currentRates = new ArrayList<>();

    @Autowired
    public CurrencyLoader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        updateRates(); // запуск при старте
    }

    @Scheduled(cron = "0 0 0 * * *") // каждый день в 00:00
    public void updateRates() {
        try {
            CurrencyRate[] rates = restTemplate.getForObject(
                    "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json",
                    CurrencyRate[].class
            );

            if (rates != null) {
                currentRates = Arrays.stream(rates)
                        .filter(rate -> List.of("USD", "EUR").contains(rate.getCc()))
                        .collect(Collectors.toList());

                // Добавляем гривну вручную
                CurrencyRate uah = new CurrencyRate("UAH", 1.0);
                currentRates.add(uah);

                System.out.println("Курсы валют обновлены: " + currentRates);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке курсов валют: " + e.getMessage());
        }
    }

    public @NotNull Optional<CurrencyRate> getRate(String currencyCode) {
        return currentRates.stream()
                .filter(rate -> rate.getCc().equalsIgnoreCase(currencyCode))
                .findFirst();
    }

    public List<CurrencyRate> getAllFilteredRates() {
        return currentRates;
    }

    public BigDecimal convert(BigDecimal amount, @NotNull String from, String to) {
        if (from.equalsIgnoreCase(to)) return amount;

        CurrencyRate fromRate = getRate(from).orElseThrow(() -> new RuntimeException("Не найден курс для: " + from));
        CurrencyRate toRate = getRate(to).orElseThrow(() -> new RuntimeException("Не найден курс для: " + to));

        BigDecimal fromToUah = amount.multiply(BigDecimal.valueOf(fromRate.getRate()));
        BigDecimal result = fromToUah.divide(BigDecimal.valueOf(toRate.getRate()), 4, RoundingMode.HALF_UP);

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    // DTO-класс для курсов
    @Getter
    @Setter
    public static final class CurrencyRate {
        private String cc;   // Валюта, например USD
        private double rate; // Курс к гривне

        public CurrencyRate() {}

        public CurrencyRate(String cc, double rate) {
            this.cc = cc;
            this.rate = rate;
        }
    }
}
