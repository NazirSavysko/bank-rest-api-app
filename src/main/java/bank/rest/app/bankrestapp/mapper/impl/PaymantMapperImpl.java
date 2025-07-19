package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.dto.PaymentDTO;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public final class PaymantMapperImpl<T, R> implements Mapper<Payment, PaymentDTO> {

    @Contract(pure = true)
    @Override
    public @NotNull PaymentDTO toDto(final @NotNull Payment entity) {
        return new PaymentDTO(
                entity.getCurrencyCode().name(),
                entity.getAmount().toString(),
                entity.getBeneficiaryName(),
                entity.getPurpose()
        );
    }
}
