package walletService.repositories;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import walletService.data.Account;
import walletService.data.Transactional;
import walletService.dto.TransactionType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class TransactionalRepositoryImplTest {
    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15");

    private static Connection connection;
    private TransactionalRepositoryImpl transactionalRepository;
    private AccountRepository accountRepository;
    private Account account;

    @BeforeAll
    static void setupDatabase() throws LiquibaseException, SQLException {
        container.start();
        connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());

        Liquibase liquibase = new Liquibase("changelog/changelog-master.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));

        liquibase.update();

    }

    @AfterAll
    static void stopDatabase() throws SQLException {
        connection.close();
        container.stop();
    }

    @BeforeEach
    void setUp() {
        transactionalRepository = new TransactionalRepositoryImpl(connection);
        accountRepository = new AccountRepositoryImpl(connection);

        account = createAccount();
        accountRepository.addNewAccount(account);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM wallet.transactional WHERE account_id = " + account.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Account createAccount(){
        Account account1 = new Account();
        account1.setId(1);
        account1.setLogin("testUser");
        account1.setPassword("password");
        account1.setFullName("Test User");
        account1.setBalanceInCents(10000);
        account1.setUniqueNumber(UUID.randomUUID());
        account1.setIsBlocked(false);
        account1.setIsDeleted(false);
        account1.setAccountCreationDate(LocalDateTime.now());
        return account1;
    }

    private Transactional transactional1(){
        return Transactional.builder()
                .balance(5000)
                .transactionNumber(UUID.randomUUID())
                .transactionType(TransactionType.DEBIT)
                .amount(5000)
                .account(account)
                .isBlocked(false)
                .transactionDate(LocalDateTime.now())
                .build();
    }

    private Transactional transactional2(){
        return Transactional.builder()
                .balance(7000)
                .transactionNumber(UUID.randomUUID())
                .transactionType(TransactionType.CREDIT)
                .amount(2000)
                .account(account)
                .isBlocked(false)
                .transactionDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetTransactionalByAccount() {
        List<Transactional> transactions = new ArrayList<>();
        transactions.add(transactional1());
        transactions.add(transactional2());

        transactions.forEach(transactional -> transactionalRepository.addTransactional(transactional));

        List<Transactional> result = transactionalRepository.getTransactionalByAccount(account);

        assertNotNull(result);
        assertEquals(transactions.size(), result.size());
        assertEquals(transactions.get(0).getTransactionNumber(), result.get(0).getTransactionNumber());
        assertEquals(transactions.get(1).getTransactionNumber(), result.get(1).getTransactionNumber());
    }

    @Test
    void testAddTransaction() {
        Transactional transaction = transactional1();

        transactionalRepository.addTransactional(transaction);

        List<Transactional> result = transactionalRepository.getTransactionalByAccount(account);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(transaction.getTransactionNumber(), result.get(0).getTransactionNumber());
    }
}
