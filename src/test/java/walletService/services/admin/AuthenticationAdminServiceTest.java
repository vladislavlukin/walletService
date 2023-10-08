package walletService.services.admin;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationAdminServiceTest {
    private static final String PASSWORD_FILE_PATH = "src/main/resources/admin_password.txt";
    private static final String PASSWORD = "admin_password";

    @Test
    void testIsCorrectPassword() throws IOException {
        String originalContent = readPasswordFile();

        writePasswordToFile(PASSWORD);

        AuthenticationAdminService authenticationAdminService = new AuthenticationAdminService(PASSWORD);
        assertTrue(authenticationAdminService.isCorrectPassword());

        authenticationAdminService = new AuthenticationAdminService("incorrect_password");
        assertFalse(authenticationAdminService.isCorrectPassword());

        authenticationAdminService = new AuthenticationAdminService("");
        assertFalse(authenticationAdminService.isCorrectPassword());

        writePasswordToFile(originalContent);
    }

    private String readPasswordFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSWORD_FILE_PATH))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString().trim();
        }
    }

    private void writePasswordToFile(String password) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD_FILE_PATH))) {
            writer.write(password);
        }
    }
}


