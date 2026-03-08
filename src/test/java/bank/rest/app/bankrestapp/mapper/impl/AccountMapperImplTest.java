package bank.rest.app.bankrestapp.mapper.impl;

import bank.rest.app.bankrestapp.currency.CurrencyLoader;
import bank.rest.app.bankrestapp.dto.get.GetAccountDTO;
import bank.rest.app.bankrestapp.dto.get.GetCardDTO;
import bank.rest.app.bankrestapp.dto.get.GetPaymentDTO;
import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.entity.Account;
import bank.rest.app.bankrestapp.entity.Card;
import bank.rest.app.bankrestapp.entity.Payment;
import bank.rest.app.bankrestapp.entity.Transaction;
import bank.rest.app.bankrestapp.entity.enums.AccountStatus;
import bank.rest.app.bankrestapp.entity.enums.AccountType;
import bank.rest.app.bankrestapp.entity.enums.Currency;
import bank.rest.app.bankrestapp.mapper.Mapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountMapperImplTest {

    @Test
    void toDto_ShouldMapEdrpou() {
        @SuppressWarnings("unchecked")
        final Mapper<Card, GetCardDTO> cardMapper = (Mapper<Card, GetCardDTO>) mock(Mapper.class);
        @SuppressWarnings("unchecked")
        final Mapper<Transaction, GetTransactionDTO> transactionMapper = (Mapper<Transaction, GetTransactionDTO>) mock(Mapper.class);
        @SuppressWarnings("unchecked")
        final Mapper<Payment, GetPaymentDTO> paymentMapper = (Mapper<Payment, GetPaymentDTO>) mock(Mapper.class);
        final CurrencyLoader currencyLoader = mock(CurrencyLoader.class);
        final AccountMapperImpl mapper = new AccountMapperImpl(cardMapper, transactionMapper, paymentMapper, currencyLoader);

        final Card card = Card.builder().cardNumber("1111").build();
        final GetCardDTO cardDto = new GetCardDTO("1111", null, "123");
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        final Account account = Account.builder()
                .accountId(1)
                .accountNumber("UA00000000000000000000000000000001")
                .balance(java.math.BigDecimal.TEN)
                .accountType(AccountType.FOP)
                .currencyCode(Currency.UAH)
                .status(AccountStatus.ACTIVE)
                .card(card)
                .edrpou("1234567890")
                .build();

        final GetAccountDTO dto = mapper.toDto(account);

        assertEquals("1234567890", dto.edrpou());
        assertEquals("UAH", dto.currency());
        assertEquals("ACTIVE", dto.status());
        assertSame(cardDto, dto.card());
    }
}
