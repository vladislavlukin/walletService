package walletService.services.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import walletService.data.Account;
import walletService.data.Transactional;
import walletService.dto.TransactionType;
import walletService.repositories.AccountRepository;
import walletService.repositories.TransactionalRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {
    private final String AMOUNT = "50";
    private TransactionalRepository transactionalRepository;
    private TransactionServiceImpl transactionService;
    private AccountRepository accountRepository;
    private Account account;

    @BeforeEach
    void setUp() {
        transactionalRepository = new TransactionalRepository();
        accountRepository = new AccountRepository();
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
        String result = transactionService.executeTransaction(AMOUNT, TransactionType.DEBIT);
        assertEquals("Operation completed", result);
    }

    @Test
    void testExecuteTransaction_SuccessfulCreditTransaction() {
        String result = transactionService.executeTransaction(AMOUNT, TransactionType.CREDIT);
        assertEquals("Operation completed", result);
    }

    @Test
    void testExecuteTransaction_FailureDebitTransaction() {
        String amount = "150";
        String result = transactionService.executeTransaction(amount, TransactionType.DEBIT);
        assertEquals("Requested amount exceeds the current balance", result);
    }

    @Test
    void testExecuteTransaction_NoCorrectAmount() {
        String amount = "150.Q";
        String result = transactionService.executeTransaction(amount, TransactionType.DEBIT);
        assertEquals("Invalid dollar amount format: " + amount, result);
    }

    @Test
    void testExecuteTransaction_InvalidAccount() {
        Account account1 = createAccount();
        account1.setIsDeleted(true);

        transactionService = new TransactionServiceImpl(accountRepository, transactionalRepository, account1);

        String result = transactionService.executeTransaction(AMOUNT, TransactionType.CREDIT);
        assertEquals("Account is not valid", result);
    }

    @Test
    void testViewTransactionHistory_ValidTransactions() {
        Transactional transaction1 = Transactional.builder()
                .balance(5000)
                .transactionNumber(UUID.randomUUID())
                .transactionType(TransactionType.DEBIT)
                .amount(5000)
                .account(account)
                .transactionDate("2023-10-07 15:30:00")
                .build();

        transactionalRepository.addTransactional(transaction1);

        Transactional transaction2 = Transactional.builder()
                .balance(7000)
                .transactionNumber(UUID.randomUUID())
                .transactionType(TransactionType.CREDIT)
                .amount(2000)
                .account(account)
                .transactionDate("2023-10-08 10:15:00")
                .build();

        transactionalRepository.addTransactional(transaction2);

        String result = transactionService.viewTransactionHistory();

        String expectedOutput = "Transaction Number: " + transaction1.getTransactionNumber() + "\n" +
                "Date: " + transaction1.getTransactionDate() + "\n" +
                "Type: " + transaction1.getTransactionType() + "\n" +
                "Amount: " + (double) transaction1.getAmount() / 100 + "\n" +
                "Balance: " + (double) transaction1.getBalance() / 100 + "\n\n" +
                "Transaction Number: " + transaction2.getTransactionNumber() + "\n" +
                "Date: " + transaction2.getTransactionDate() + "\n" +
                "Type: " + transaction2.getTransactionType() + "\n" +
                "Amount: " + (double) transaction2.getAmount() / 100 + "\n" +
                "Balance: " + (double) transaction2.getBalance() / 100 + "\n\n";

        assertEquals(expectedOutput, result);
    }

    @Test
    void testViewTransactionHistory_EmptyTransactions() {
        String result = transactionService.viewTransactionHistory();

        assertEquals("You don't have transaction", result);
    }

}



