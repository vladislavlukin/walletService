package walletService.services.admin;

import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Сервис аутентификации администратора для проверки пароля.
 */
@AllArgsConstructor
public class AuthenticationAdminService {
    private final String password;

    /**
     * Проверяет, является ли указанный пароль правильным паролем администратора.
     *
     * @return true, если пароль правильный, в противном случае - false.
     */
    public boolean isCorrectPassword() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/admin_password.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
