package walletService.services.user;

import lombok.AllArgsConstructor;
import walletService.data.Account;
import walletService.repositories.AccountRepository;

/**
 * Класс, представляющий сервис аутентификации для проверки учетных данных пользователя.
 */
@AllArgsConstructor
public class AuthenticationService{
    private final AccountRepository accountRepository;
    private final String name;
    private final String password;

    /**
     * Возвращает аккаунт пользователя, соответствующий учетным данным (имени и паролю), из репозитория.
     *
     * @return Объект типа Account, представляющий аутентифицированного пользователя,
     *         или null, если пользователь не найден или аутентификация не удалась.
     */
    public Account getAuthenticationAccount(){
        return accountRepository.getAccountByLoginAndPassword(name, password);
    }

}
