package walletService.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import walletService.data.Account;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private AccountRepository accountRepository;
    private Account account;

    @BeforeEach
    void setUp() {
        accountRepository = new AccountRepository();
        account = new Account();

        account.setLogin("testUser");
        account.setPassword("password");
        account.setFullName("Test User");
        account.setBalanceInCents(10000);

        accountRepository.addNewAccount(account);
    }

    @Test
    void testGetAccountByLoginAndPassword() {
        Account testAccount = accountRepository.getAccountByLoginAndPassword(account.getLogin(), account.getPassword());
        Account testAccount_InvalidLoginAndPassword = accountRepository.getAccountByLoginAndPassword(account.getLogin(), "error");

        assertNotNull(testAccount);
        assertEquals(account, testAccount);
        assertNotEquals(account, testAccount_InvalidLoginAndPassword);
    }

    @Test
    void testGetAccountByLogin() {
        Account testAccount = accountRepository.getAccountByLogin(account.getLogin());
        Account testAccount_InvalidLoginAndPassword = accountRepository.getAccountByLogin("error");

        assertNotNull(testAccount);
        assertEquals(account, testAccount);
        assertNotEquals(account, testAccount_InvalidLoginAndPassword);
    }

    @Test
    void testIsLoginAlreadyExists() {
        boolean result = accountRepository.isLoginAlreadyExists(account.getLogin());
        boolean result_InvalidLogin = accountRepository.isLoginAlreadyExists("error");

        assertTrue(result);
        assertFalse(result_InvalidLogin);
    }

    @Test
    void testAddNewAccount() {
        Account result = accountRepository.getAccountByLogin(account.getLogin());

        assertNotNull(result);
        assertEquals(account.getLogin(), result.getLogin());
    }

    @Test
    public void testUpdateAccountByAmount() {
        long newBalance = 3000;
        Account updatedAccount = accountRepository.updateAccountByAmount(account, newBalance);

        assertEquals(newBalance, updatedAccount.getBalanceInCents());
    }

}

