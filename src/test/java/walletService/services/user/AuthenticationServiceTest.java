package walletService.services.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import walletService.data.Account;
import walletService.repositories.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {
    private AuthenticationService authenticationService;
    private AccountRepository accountRepository;
    private Account account;

    @BeforeEach
    void setUp() {
        accountRepository = new AccountRepository();
        account = createAccount();
    }

    private Account createAccount() {
        Account account = new Account();
        account.setLogin("testUser");
        account.setPassword("password");
        account.setFullName("Test User");
        accountRepository.addNewAccount(account);
        return account;
    }

    @Test
    void testGetResponse_ValidCredentials() {
        authenticationService = new AuthenticationService(accountRepository, account.getLogin(), account.getPassword());

        Account testAccount = authenticationService.getAuthenticationAccount();

        assertEquals(account, testAccount);
    }

    @Test
    void testGetResponse_InvalidCredentials() {
        authenticationService = new AuthenticationService(accountRepository, account.getLogin(), "error");

        Account testAccount = authenticationService.getAuthenticationAccount();

        assertNull(testAccount);
    }

}

