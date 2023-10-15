package walletService.repositories;

import walletService.data.Account;

import java.util.List;

/**
 * Интерфейс {@code AccountRepository} представляет собой репозиторий для управления учетными записями пользователей.
 * Он предоставляет методы для получения учетных записей, проверки существования логина и добавления новых учетных записей.
 */
public interface AccountRepository {

    /**
     * Получает все записи.
     *
     * @return получает список всех записей или null, если не найдена.
     */
    List<Account> getAccounts();

    /**
     * Получает учетную запись по логину и паролю.
     *
     * @param name     Логин учетной записи.
     * @param password Пароль учетной записи.
     * @return Учетная запись с указанным логином и паролем или null, если не найдена.
     */
    Account getAccountByLoginAndPassword(String name, String password);

    /**
     * Получает учетную запись по логину.
     *
     * @param name Логин учетной записи.
     * @return Учетная запись с указанным логином или null, если не найдена.
     */
    Account getAccountByLogin(String name);

    /**
     * Обновляет баланс аккаунта путем изменения суммы денег и возвращает новый аккаунт.
     *
     * @param account Аккаунт, который должен быть обновлен.
     * @param balance Баланс аккаунта после выполнения транзакции.
     * @return Новый аккаунт с обновленным балансом или null, если аккаунт с указанными данными не найден.
     */
    Account updateAccountByAmount(Account account, long balance);

    /**
     * Проверяет, существует ли уже логин.
     *
     * @param login Логин для проверки.
     * @return true, если логин уже существует, в противном случае - false.
     */
    boolean isLoginAlreadyExists(String login);

    /**
     * Добавляет новую учетную запись.
     *
     * @param account Учетная запись для добавления.
     */
    void addNewAccount(Account account);

}


