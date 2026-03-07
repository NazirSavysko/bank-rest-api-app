package bank.rest.app.bankrestapp.resository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface HistoryItemProjection {

    Integer getId();

    BigDecimal getAmount();

    String getCurrency();

    LocalDateTime getCreatedAt();

    String getType();

    Integer getSenderAccountId();

    Integer getReceiverAccountId();

    String getDetails();
}
