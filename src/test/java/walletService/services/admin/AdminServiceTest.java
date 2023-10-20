package walletService.services.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import walletService.data.Account;
import walletService.dto.AdminResponse;
import walletService.exceptions.DatabaseException;
import walletService.repositories.AccountRepository;
import walletService.repositories.TransactionalRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


public class AdminServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionalRepository transactionalRepository;
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() throws DatabaseException {
        MockitoAnnotations.openMocks(this);

        Account account1 = createAccount("johndoe");
        when(accountRepository.getAccountByLogin(account1.getLogin())).thenReturn(account1);

        Account account2 = createAccount("alicesmith");
        when(accountRepository.getAccountByLogin(account2.getLogin())).thenReturn(account2);

        List<Account> allAccounts = new ArrayList<>();
        allAccounts.add(account2);
        allAccounts.add(account1);
        when(accountRepository.getAccounts()).thenReturn(allAccounts);


        adminService = new AdminServiceImpl(accountRepository, transactionalRepository);

    }

    private Account createAccount(String login){
        Account account = new Account();
        account.setLogin(login);
        account.setIsDeleted(false);
        account.setIsBlocked(false);

        return account;
    }

    @Test
    void testGetAdminResponse_ValidLogin() throws DatabaseException {
        System.out.println(accountRepository.getAccounts());
        AdminResponse response = adminService.getAdminResponse("johndoe");

        assertTrue(response.isResult());
    }
    @Test
    void testGetAdminResponse_InvalidLogin() throws DatabaseException {
        AdminResponse response = adminService.getAdminResponse("johndo");

        assertFalse(response.isResult());
    }

    @Test
    void testGetAdminResponse() throws DatabaseException {
        AdminResponse response = adminService.getAdminResponse("");

        assertTrue(response.isResult());
    }
}

