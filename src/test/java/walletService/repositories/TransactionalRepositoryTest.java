package walletService.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import walletService.data.Account;
import walletService.data.Transactional;
import walletService.dto.TransactionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionalRepositoryTest {
    private TransactionalRepository transactionalRepository;
    private Account account;

    @BeforeEach
    void setUp() {
        transactionalRepository = new TransactionalRepository();
        AccountRepository accountRepository = new AccountRepository();
        account = new Account();

        account.setLogin("testUser");
        account.setPassword("password");
        account.setFullName("Test User");

        accountRepository.addNewAccount(account);
    }

    @Test
    void testGetTransactionalByAccount() {
        List<Transactional> transactions = new ArrayList<>();

        Transactional transaction1 = Transactional.builder()
                .balance(5000)
                .transactionNumber(UUID.randomUUID())
                .transactionType(TransactionType.DEBIT)
                .amount(5000)
                .account(account)
                .transactionDate("2023-10-07 15:30:00")
                .build();
        transactions.add(transaction1);


        Transactional transaction2 = Transactional.builder()
                .balance(7000)
                .transactionNumber(UUID.randomUUID())
                .transactionType(TransactionType.CREDIT)
                .amount(2000)
                .account(account)
                .transactionDate("2023-10-08 10:15:00")
                .build();
        transactions.add(transaction2);

        transactionalRepository.addTransactional(transaction1);
        transactionalRepository.addTransactional(transaction2);

        List<Transactional> result = transactionalRepository.getTransactionalByAccount(account);

        assertNotNull(result);
        assertEquals(transactions.size(), result.size());
        assertEquals(transactions.get(0).getTransactionNumber(), result.get(0).getTransactionNumber());
        assertEquals(transactions.get(1).getTransactionNumber(), result.get(1).getTransactionNumber());
    }

    @Test
    void testAddTransaction() {
        Transactional transaction = Transactional.builder()
                .balance(5000)
                .transactionNumber(UUID.randomUUID())
                .transactionType(TransactionType.DEBIT)
                .amount(5000)
                .account(account)
                .transactionDate("2023-10-07 15:30:00")
                .build();

        transactionalRepository.addTransactional(transaction);

        List<Transactional> result = transactionalRepository.getTransactionalByAccount(account);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction.getTransactionNumber(), result.get(0).getTransactionNumber());
    }
}
