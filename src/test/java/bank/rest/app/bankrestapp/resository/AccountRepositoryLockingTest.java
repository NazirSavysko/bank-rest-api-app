package bank.rest.app.bankrestapp.resository;

import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountRepositoryLockingTest {

    @Test
    void lockingQueries_ShouldUsePessimisticWriteLock() throws NoSuchMethodException {
        assertPessimisticWrite(AccountRepository.class.getMethod("findByAccountNumberForUpdate", String.class));
        assertPessimisticWrite(AccountRepository.class.getMethod("findByCard_CardNumberForUpdate", String.class));
        assertPessimisticWrite(AccountRepository.class.getMethod("findByIdForUpdate", Integer.class));
    }

    private void assertPessimisticWrite(final Method method) {
        final Lock lock = method.getAnnotation(Lock.class);
        assertNotNull(lock, () -> method.getName() + " should declare @Lock");
        assertEquals(LockModeType.PESSIMISTIC_WRITE, lock.value());
    }
}
