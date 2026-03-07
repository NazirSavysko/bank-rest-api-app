package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.get.TransactionHistoryDirection;
import bank.rest.app.bankrestapp.dto.get.TransactionHistoryItemDTO;
import bank.rest.app.bankrestapp.dto.get.TransactionHistoryType;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    private TransactionFacade transactionFacade;
    private TransactionController controller;

    @BeforeEach
    void setUp() {
        this.transactionFacade = mock(TransactionFacade.class);
        this.controller = new TransactionController(transactionFacade);
    }

    @Test
    void getTransactionHistory_shouldReturnFacadeResult() {
        final Integer accountId = 7;
        final Pageable pageable = Pageable.unpaged();
        final TransactionHistoryItemDTO item = new TransactionHistoryItemDTO(
                1L,
                TransactionHistoryType.TRANSFER,
                TransactionHistoryDirection.INCOME,
                BigDecimal.valueOf(100),
                "USD",
                "COMPLETED",
                "Transfer",
                null,
                null,
                null,
                null,
                null
        );
        final Page<TransactionHistoryItemDTO> expectedPage = new PageImpl<>(List.of(item), pageable, 1);

        when(transactionFacade.getTransactionHistory(pageable, accountId)).thenReturn(expectedPage);

        final Page<TransactionHistoryItemDTO> result = controller.getTransactionHistory(pageable, accountId);

        assertEquals(expectedPage, result);
        verify(transactionFacade).getTransactionHistory(pageable, accountId);
    }
}
