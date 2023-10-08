package walletService.services.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import walletService.data.Account;
import walletService.dto.AdminResponse;
import walletService.repositories.AccountRepository;
import walletService.repositories.TransactionalRepository;

import static org.junit.jupiter.api.Assertions.*;


public class AdminServiceTest {
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        AccountRepository accountRepository = new AccountRepository();
        TransactionalRepository transactionalRepository = new TransactionalRepository();

        Account account = createAccount("johndoe");
        accountRepository.addNewAccount(account);

        account = createAccount("alicesmith");
        accountRepository.addNewAccount(account);

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
    void testGetAdminResponse_ValidLogin() {
        AdminResponse response = adminService.getAdminResponse("johndoe");

        assertTrue(response.isResult());
    }
    @Test
    void testGetAdminResponse_InvalidLogin() {
        AdminResponse response = adminService.getAdminResponse("johndo");

        assertFalse(response.isResult());
    }

    @Test
    void testGetAdminResponse() {
        AdminResponse response = adminService.getAdminResponse("");

        assertTrue(response.isResult());
    }
}

