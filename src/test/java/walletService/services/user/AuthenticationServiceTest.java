package walletService.services.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import walletService.data.Account;
import walletService.repositories.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {
    private AuthenticationService authenticationService;
    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(accountRepository, "testUser", "password");
    }

    @Test
    void testGetResponse_ValidCredentials() {
        Account testAccount = new Account();
        testAccount.setLogin("testUser");
        testAccount.setPassword("password");

        when(accountRepository.getAccountByLoginAndPassword("testUser", "password")).thenReturn(testAccount);

        Account result = authenticationService.getAuthenticationAccount();

        assertNotNull(result);
        assertEquals("testUser", result.getLogin());
        assertEquals("password", result.getPassword());
    }

    @Test
    void testGetResponse_InvalidCredentials() {
        when(accountRepository.getAccountByLoginAndPassword("testUser", "password")).thenReturn(null);

        Account result = authenticationService.getAuthenticationAccount();

        assertNull(result);
    }

}

