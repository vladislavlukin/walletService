package walletService.repositories;

import lombok.Getter;
import walletService.data.Account;

import java.util.*;

/**
 * Класс {@code AccountRepository} представляет собой репозиторий для управления учетными записями пользователей.
 * Он предоставляет методы для получения учетных записей, проверки существования логина и добавления новых учетных записей.
 */
@Getter
public class AccountRepository {
    private final List<Account> accounts = new ArrayList<>();

    /**
     * Получает учетную запись по логину и паролю.
     *
     * @param name     Логин учетной записи.
     * @param password Пароль учетной записи.
     * @return Учетная запись с указанным логином и паролем или null, если не найдена.
     */
    public Account getAccountByLoginAndPassword(String name, String password){
        return accounts
                .stream()
                .filter(account -> account.getLogin().equals(name) && account.getPassword().equals(password))
                .findFirst()
                .orElse(null);

    }

    /**
     * Получает учетную запись по логину.
     *
     * @param name Логин учетной записи.
     * @return Учетная запись с указанным логином или null, если не найдена.
     */
    public Account getAccountByLogin(String name){
        return accounts
                .stream()
                .filter(account -> account.getLogin().equals(name))
                .findFirst()
                .orElse(null);

    }

    /**
     * Обновляет баланс аккаунта путем изменения суммы денег и возвращает новый аккаунт.
     *
     * @param account Аккаунт, который должен быть обновлен.
     * @param balance Баланс аккаунта после выполнения транзакции.
     * @return Новый аккаунт с обновленным балансом или null, если аккаунт с указанными данными не найден.
     */
    public Account updateAccountByAmount(Account account, long balance) {
        return accounts
                .stream()
                .filter(acc -> acc.equals(account))
                .peek(acc -> {
                    acc.setBalanceInCents(balance);
                })
                .findFirst()
                .orElse(null);
    }


    /**
     * Проверяет, существует ли уже логин.
     *
     * @param login Логин для проверки.
     * @return true, если логин уже существует, в противном случае - false.
     */
    public boolean isLoginAlreadyExists(String login) {
        return login != null && accounts
                .stream()
                .anyMatch(account -> account.getLogin().equals(login));
    }

    /**
     * Добавляет новую учетную запись.
     *
     * @param account Учетная запись для добавления.
     */
    public void addNewAccount(Account account) {
        accounts.add(account);
    }

}
