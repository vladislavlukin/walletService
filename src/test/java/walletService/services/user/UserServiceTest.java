package walletService.services.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import walletService.dto.UserResponse;
import walletService.exceptions.DatabaseException;
import walletService.repositories.AccountRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

class UserServiceTest {
    @Mock
    private AccountRepository accountRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(accountRepository);
    }

    private void setInput(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }

    @Test
    void testGetResponse() throws DatabaseException {
        String userInput = "John Doe\nmylogin\nmypassword\n";

        setInput(userInput);

        when(accountRepository.isLoginAlreadyExists(anyString())).thenReturn(false);

        UserResponse response = userService.getResponse();

        assertTrue(response.isResult());
        assertNotNull(response.getText());
    }
}

