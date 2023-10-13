package walletService.services.user;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import walletService.dto.UserResponse;
import walletService.repositories.AccountRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

class UserServiceTest {
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        AccountRepository accountRepository = new AccountRepository();
        userService = new UserServiceImpl(accountRepository);
    }

    private void setInput(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }

    @Test
    void testGetResponse() {
        String userInput = "John Doe\nmylogin\nmypassword\n";

        setInput(userInput);

        UserResponse response = userService.getResponse();

        assertTrue(response.isResult());
        assertNotNull(response.getText());
    }
}

