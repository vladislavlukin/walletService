package walletService.repositories;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import walletService.data.Account;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class AccountRepositoryImplTest {
    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15");

    private static Connection connection;
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
        accountRepository = new AccountRepositoryImpl(connection);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM wallet.account WHERE login = 'testUser'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Account createAccount(){
        Account account1 = new Account();
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

    @Test
    void testAddNewAccount() {
        account = createAccount();
        accountRepository.addNewAccount(account);

        List<Account> result = accountRepository.getAccounts();

        assertNotNull(result);
    }

    @Test
    void testGetAccountByLoginAndPassword() {
        account = createAccount();
        accountRepository.addNewAccount(account);

        Account testAccount = accountRepository.getAccountByLoginAndPassword(account.getLogin(), account.getPassword());
        Account testAccount_InvalidLoginAndPassword = accountRepository.getAccountByLoginAndPassword(account.getLogin(), "error");

        assertNotNull(testAccount);
        assertEquals(account.getLogin(), testAccount.getLogin());
        assertNotEquals(account, testAccount_InvalidLoginAndPassword);
    }

    @Test
    void testGetAccountByLogin() {
        account = createAccount();
        accountRepository.addNewAccount(account);

        Account testAccount = accountRepository.getAccountByLogin(account.getLogin());
        Account testAccount_InvalidLoginAndPassword = accountRepository.getAccountByLogin("error");

        assertNotNull(testAccount);
        assertEquals(account.getLogin(), testAccount.getLogin());
        assertNull(testAccount_InvalidLoginAndPassword);
    }

    @Test
    void testIsLoginAlreadyExists() {
        account = createAccount();
        accountRepository.addNewAccount(account);

        boolean result = accountRepository.isLoginAlreadyExists(account.getLogin());
        boolean result_InvalidLogin = accountRepository.isLoginAlreadyExists("error");

        assertTrue(result);
        assertFalse(result_InvalidLogin);
    }

    @Test
    public void testUpdateAccountByAmount() {
        account = createAccount();
        accountRepository.addNewAccount(account);

        Account account1 = accountRepository.getAccountByLogin("testUser");

        long newBalance = 3000;

        Account updatedAccount = accountRepository.updateAccountByAmount(account1, newBalance);

        assertEquals(newBalance, updatedAccount.getBalanceInCents());
    }

}

