package walletService.services.user;

import lombok.AllArgsConstructor;
import walletService.data.Account;
import walletService.dto.UserResponse;
import walletService.repositories.AccountRepository;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

/**
 * Реализация интерфейса {@code UserService} для создания нового пользователя и управления данными пользователя.
 */
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final AccountRepository accountRepository;
    private final Account account = new Account();

    @Override
    public UserResponse getResponse() {
        Scanner scanner = new Scanner(System.in);

        enterName(scanner);
        enterLogin(scanner);
        enterPassword(scanner);

        UUID uniqueNumber = UUID.randomUUID();

        addNewAccount(uniqueNumber);

        return UserResponse.builder()
                .result(true)
                .text("\nYour transaction ID - " + uniqueNumber)
                .build();
    }

    /**
     * Получает от пользователя имя и устанавливает его в аккаунт.
     *
     * @param scanner Сканнер для ввода данных от пользователя.
     */
    private void enterName(Scanner scanner) {
        System.out.println("Please enter your name:");
        UserResponse userResponse = setName(scanner.nextLine().trim());
        System.out.println(userResponse.getText());
        if (!userResponse.isResult()) {
            enterName(scanner);
        }
    }

    /**
     * Получает от пользователя логин и устанавливает его в аккаунт, проверяя его уникальность.
     *
     * @param scanner Сканнер для ввода данных от пользователя.
     */
    private void enterLogin(Scanner scanner) {
        UserResponse userResponse = setLogin(scanner.nextLine().trim());
        System.out.println(userResponse.getText());
        if (!userResponse.isResult()) {
            enterLogin(scanner);
        }
    }

    /**
     * Получает от пользователя пароль и устанавливает его в аккаунт.
     *
     * @param scanner Сканнер для ввода данных от пользователя.
     */
    private void enterPassword(Scanner scanner) {
        UserResponse userResponse = setPassword(scanner.nextLine().trim());
        System.out.println(userResponse.getText());
        if (!userResponse.isResult()) {
            enterPassword(scanner);
        }
    }

    /**
     * Устанавливает имя пользователя в аккаунт, если оно соответствует допустимому формату.
     *
     * @param name Имя пользователя.
     * @return Результат операции и текстовое сообщение.
     */
    private UserResponse setName(String name) {
        boolean result = true;
        String text = "Now let's create a login.";
        if (isValidNameFormat(name)) {
            account.setFullName(name);
        } else {
            result = false;
            text = "Invalid format! Example input: \"John Doe\". Let's try again :)";
        }
        return UserResponse.builder()
                .result(result)
                .text(text)
                .build();
    }

    /**
     * Устанавливает логин пользователя в аккаунт, если он уникальный.
     *
     * @param login Логин пользователя.
     * @return Результат операции и текстовое сообщение.
     */
    private UserResponse setLogin(String login) {
        boolean result = true;
        String text = "Great, we're almost done! Now, please set a password (minimum 4 characters):";
        if (!accountRepository.isLoginAlreadyExists(login)) {
            account.setLogin(login);
        } else {
            result = false;
            text = "Oops, this login already exists. Please try again.";
        }
        return UserResponse.builder()
                .result(result)
                .text(text)
                .build();
    }

    /**
     * Устанавливает пароль пользователя в аккаунт, если он соответствует минимальной длине.
     *
     * @param password Пароль пользователя.
     * @return Результат операции и текстовое сообщение.
     */
    private UserResponse setPassword(String password) {
        int minPasswordLength = 4;
        boolean result = true;
        String text = "Great! You are now registered and can use our services!";
        if (password.length() >= minPasswordLength) {
            account.setPassword(password);
        } else {
            result = false;
            text = "Too short ;) Please enter a password with at least 4 characters.";
        }
        return UserResponse.builder()
                .result(result)
                .text(text)
                .build();
    }

    /**
     * Добавляет новый аккаунт с уникальным номером, датой создания и начальным балансом.
     *
     * @param uniqueNumber Уникальный номер пользователя.
     */
    private void addNewAccount(UUID uniqueNumber) {
        account.setUniqueNumber(uniqueNumber);
        account.setAccountCreationDate(LocalDateTime.now());
        account.setBalanceInCents(0);
        account.setIsBlocked(false);
        account.setIsDeleted(false);
        accountRepository.addNewAccount(account);
    }

    /**
     * Проверяет, соответствует ли имя пользователя допустимому формату.
     *
     * @param name Имя пользователя.
     * @return true, если имя соответствует формату, иначе false.
     */
    private boolean isValidNameFormat(String name) {
        return name != null && name.matches("^[A-Za-zА-Яа-яЁё\\s]+$");
    }

}

