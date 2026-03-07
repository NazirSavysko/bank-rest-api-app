package bank.rest.app.bankrestapp.resository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionHistoryProjection {

    String getItemType();

    Long getOperationId();

    BigDecimal getAmount();

    String getCurrencyCode();

    String getStatus();

    String getDescription();

    LocalDateTime getCreatedAt();

    Integer getSenderAccountId();

    Integer getReceiverAccountId();

    String getRecipientIban();

    String getRecipientName();

    String getProviderName();

    String getContractNumber();
}
