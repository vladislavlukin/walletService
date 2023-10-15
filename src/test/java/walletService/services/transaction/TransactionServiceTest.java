package walletService.services.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import walletService.data.Account;
import walletService.data.Transactional;
import walletService.dto.TransactionType;
import walletService.repositories.AccountRepository;
import walletService.repositories.TransactionalRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    private final String AMOUNT = "50";
    @Mock
    private TransactionalRepository transactionalRepository;
    @Mock
    private AccountRepository accountRepository;
    private TransactionService transactionService;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account = createAccount();
        transactionService = new TransactionServiceImpl(accountRepository, transactionalRepository, account);
    }

    private Account createAccount() {
        Account account = new Account();
        account.setBalanceInCents(10000);
        account.setIsBlocked(false);
        account.setIsDeleted(false);
        return account;
    }

    @Test
    void testExecuteTransaction_SuccessfulDebitTransaction() {
        doNothing().when(transactionalRepository).addTransactional(any(Transactional.class));

        String result = transactionService.executeTransaction(AMOUNT, TransactionType.DEBIT);

        assertEquals("Operation completed", result);
        verify(transactionalRepository, times(1)).addTransactional(any(Transactional.class));
        verify(accountRepository, times(1)).updateAccountByAmount(eq(account), anyLong());
    }

    @Test
    void testExecuteTransaction_SuccessfulCreditTransaction() {
        doNothing().when(transactionalRepository).addTransactional(any(Transactional.class));

        String result = transactionService.executeTransaction(AMOUNT, TransactionType.CREDIT);

        assertEquals("Operation completed", result);
        verify(transactionalRepository, times(1)).addTransactional(any(Transactional.class));
        verify(accountRepository, times(1)).updateAccountByAmount(eq(account), anyLong());
    }

    @Test
    void testExecuteTransaction_FailureDebitTransaction() {
        String amount = "150";
        String result = transactionService.executeTransaction(amount, TransactionType.DEBIT);

        assertEquals("Requested amount exceeds the current balance", result);
        verify(transactionalRepository, times(0)).addTransactional(any(Transactional.class));
        verify(accountRepository, times(0)).updateAccountByAmount(eq(account), anyLong());
    }


    @Test
    void testExecuteTransaction_NoCorrectAmount() {
        String amount = "150.Q";
        String result = transactionService.executeTransaction(amount, TransactionType.DEBIT);

        assertTrue(result.startsWith("Invalid dollar amount format"));
        verify(transactionalRepository, times(0)).addTransactional(any(Transactional.class));
        verify(accountRepository, times(0)).updateAccountByAmount(eq(account), anyLong());
    }

    @Test
    void testViewTransactionHistory_EmptyTransactions() {
        when(transactionalRepository.getTransactionalByAccount(eq(account))).thenReturn(List.of());

        String result = transactionService.viewTransactionHistory();

        assertEquals("You don't have transaction", result);
    }

    @Test
    void testViewTransactionHistory_ValidTransactions() {
        List<Transactional> transactions = createSampleTransactions();
        when(transactionalRepository.getTransactionalByAccount(eq(account))).thenReturn(transactions);

        String result = transactionService.viewTransactionHistory();

        assertTrue(result.contains("Type: DEBIT"));
        assertTrue(result.contains("Amount: 50.0"));
        assertTrue(result.contains("Balance: 950.0"));
        assertTrue(result.contains("Type: CREDIT"));
        assertTrue(result.contains("Amount: 25.0"));
        assertTrue(result.contains("Balance: 975.0"));
    }

    private List<Transactional> createSampleTransactions() {
        Transactional transaction1 = Transactional.builder()
                .transactionDate(LocalDateTime.now())
                .isBlocked(false)
                .amount(5000)
                .balance(95000)
                .transactionNumber(UUID.randomUUID())
                .transactionType(TransactionType.DEBIT)
                .account(account)
                .build();

        Transactional transaction2 = Transactional.builder()
                .transactionDate(LocalDateTime.now())
                .isBlocked(false)
                .amount(2500)
                .balance(97500)
                .transactionNumber(UUID.randomUUID())
                .transactionType(TransactionType.CREDIT)
                .account(account)
                .build();

        return List.of(transaction1, transaction2);
    }

}



