package bank.rest.app.bankrestapp.controller;

import bank.rest.app.bankrestapp.dto.get.GetTransactionDTO;
import bank.rest.app.bankrestapp.facade.TransactionFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionControllerTest {

    private TransactionFacade transactionFacade;
    private TransactionController controller;

    @BeforeEach
    void setUp() {
        this.transactionFacade = mock(TransactionFacade.class);
        this.controller = new TransactionController(transactionFacade);
    }

    @Test
    void getAllTransactions_ShouldPassPageRequestDescAndAccountNumber() {
        final String accountNumber = "UA123456789";
        final GetTransactionDTO dto = mock(GetTransactionDTO.class);
        final Page<GetTransactionDTO> expectedPage = new PageImpl<>(List.of(dto));

        when(transactionFacade.getAllTransactions(
                argThat(p -> p.getPageNumber() == 0
                        && p.getPageSize() == 10
                        && p.getSort().getOrderFor("transactionDate") != null
                        && p.getSort().getOrderFor("transactionDate").getDirection() == Sort.Direction.DESC),
                eq(accountNumber)
        )).thenReturn(expectedPage);

        final Page<GetTransactionDTO> result = controller.getAllTransactions(0, 10, accountNumber);

        assertEquals(expectedPage, result);
        verify(transactionFacade).getAllTransactions(
                argThat(p -> p.getPageNumber() == 0
                        && p.getPageSize() == 10
                        && p.getSort().getOrderFor("transactionDate").getDirection() == Sort.Direction.DESC),
                eq(accountNumber)
        );
    }

    @Test
    void getAllTransactions_ShouldRespectPageAndSizeParams() {
        final String accountNumber = "UA987654321";
        final Page<GetTransactionDTO> expectedPage = new PageImpl<>(List.of());

        when(transactionFacade.getAllTransactions(
                argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 5),
                eq(accountNumber)
        )).thenReturn(expectedPage);

        final Page<GetTransactionDTO> result = controller.getAllTransactions(2, 5, accountNumber);

        assertEquals(expectedPage, result);
        verify(transactionFacade).getAllTransactions(
                argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 5),
                eq(accountNumber)
        );
    }
}
