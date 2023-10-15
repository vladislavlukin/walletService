package walletService;

import walletService.connect.DatabaseConnection;
import walletService.data.Account;
import walletService.repositories.AccountRepository;
import walletService.repositories.AccountRepositoryImpl;
import walletService.dto.TransactionType;
import walletService.repositories.TransactionalRepository;
import walletService.repositories.TransactionalRepositoryImpl;
import walletService.services.admin.AdminService;
import walletService.services.admin.AdminServiceImpl;
import walletService.services.admin.AuthenticationAdminService;
import walletService.services.transaction.TransactionService;
import walletService.services.transaction.TransactionServiceImpl;
import walletService.services.user.AuthenticationService;
import walletService.services.user.UserService;
import walletService.services.user.UserServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.UUID;

/**
 * Главный класс приложения, управляющий взаимодействием с пользователем.
 */
public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        TransactionalRepository transactionalRepository = new TransactionalRepositoryImpl(DatabaseConnection.getConnection());
        AccountRepository accountRepository = new AccountRepositoryImpl(DatabaseConnection.getConnection());
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome!");

        while (true) {
            System.out.println("\nAre you a registered user? Answer: yes/no");
            String response = scanner.nextLine().trim();

            switch (response) {
                case "yes" -> {
                    Account account = authenticateUser(accountRepository, scanner);
                    if(account == null){
                     continue;
                    }
                    handleRegisteredUser(account, accountRepository, transactionalRepository, scanner);
                }
                case "no" -> {
                    UserService userService = new UserServiceImpl(accountRepository);

                    System.out.println(userService.getResponse().getText());
                }
                case "admin" -> {
                    if(isAuthenticationAdmin(scanner)){
                        handleAdmin(accountRepository, transactionalRepository, scanner);
                    }
                }
                default -> System.out.println("Invalid response, let's start over.");

            }

        }
    }

    /**
     * Метод аутентифицирует пользователя, запрашивая его логин и пароль.
     *
     * @param accountRepository    Репозиторий данных для информации о пользователях.
     * @param scanner Объект Scanner для считывания ввода пользователя.
     * @return Аутентифицированный аккаунт в случае успеха или null, если аутентификация не удалась.
     */
    private static Account authenticateUser(AccountRepository accountRepository, Scanner scanner) {
        System.out.println("Enter your login:");
        String username = scanner.nextLine().trim();
        System.out.println("Enter your password:");
        String password = scanner.nextLine().trim();

        AuthenticationService authenticationService = new AuthenticationService(accountRepository, username, password);

        Account authenticatedAccount = authenticationService.getAuthenticationAccount();
        if (authenticatedAccount == null) {
            System.out.println("Invalid password or login. Let's try again.");
            return null;
        }

        return authenticatedAccount;
    }


    /**
     * Обработка действий для зарегистрированного пользователя.
     *
     * @param transactionalRepository   Репозиторий данных для доступа к информации о транзакциях.
     * @param scanner Объект Scanner для чтения ввода пользователя.
     */
    private static void handleRegisteredUser(Account account, AccountRepository accountRepository,
                                             TransactionalRepository transactionalRepository, Scanner scanner) {
        System.out.println("\nWelcome, " + account.getFullName() +
                "\nYour balance: " + (double) account.getBalanceInCents()/100 + " $");

        boolean exit = true;
        while (exit) {
            System.out.println("\nSelect an action:\n");
            System.out.println("1 - Withdraw Money");
            System.out.println("2 - Deposit Money");
            System.out.println("3 - View All Transactions");
            System.out.println("4 - Exit");

            TransactionService transactionService = new TransactionServiceImpl(accountRepository, transactionalRepository, account);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1", "2" -> {
                    System.out.println("Enter your transaction ID");
                    UUID uniqueNumber = readAndValidateUniqueNumber(scanner.nextLine().trim(), account);

                    if (uniqueNumber == null) {
                        continue;
                    }

                    System.out.println("Enter the amount");
                    String amount = scanner.nextLine().trim();

                    TransactionType transactionType = (choice.equals("1")) ? TransactionType.DEBIT : TransactionType.CREDIT;
                    System.out.println(transactionService.executeTransaction(amount, transactionType));

                }
                case "3" -> System.out.println(transactionService.viewTransactionHistory());
                case "4" -> exit = false;
                default -> System.out.println("Invalid response");
            }
        }
    }

    /**
     * Метод выполняет аутентификацию администратора по паролю.
     *
     * @param scanner Объект Scanner для считывания ввода администратора.
     * @return true, если аутентификация администратора успешна, false в противном случае.
     */
    private static boolean isAuthenticationAdmin(Scanner scanner) {
        System.out.println("Enter your password");

        AuthenticationAdminService authenticationAdminService = new AuthenticationAdminService(scanner.nextLine().trim());

        if (!authenticationAdminService.isCorrectPassword()) {
            System.out.println("Invalid password. Let's try again.");
            return false;
        }

        return true;
    }


    /**
     * Обработка действий для администратора.
     *
     * @param accountRepository   Репозиторий данных для доступа к информации о пользователях.
     * @param scanner Объект Scanner для чтения ввода пользователя.
     */
    private static void handleAdmin(AccountRepository accountRepository, TransactionalRepository transactionalRepository, Scanner scanner) {
        System.out.println("\nWelcome, Admin");

        AdminService adminService = new AdminServiceImpl(accountRepository, transactionalRepository);

        boolean exit = true;
        while (exit) {
            System.out.println("\nEnter the criteria for generating a report:\n");
            System.out.println("Leave the field empty - audit for all users");
            System.out.println("Enter the user's login - audit for a specific user's login");
            System.out.println("Enter 'exit' to exit");

            String choice = scanner.nextLine().trim();
            String response = "";

            switch (choice) {
                case "" -> response = adminService.getAdminResponse("").getText();
                case "exit" -> exit = false;
                default -> response = adminService.getAdminResponse(choice).getText();
            }

            System.out.println(response);

        }
    }

    /**
     * Считывает и проверяет уникальный идентификатор транзакции, предоставленный в виде строки.
     * Метод выполняет проверку на соответствие формату UUID и сравнивает полученный идентификатор с уникальным
     * идентификатором, связанным с переданным аккаунтом.
     *
     * @param number Строка, представляющая уникальный идентификатор транзакции для проверки.
     * @param account Аккаунт, для которого выполняется проверка уникального идентификатора транзакции.
     * @return Проверенный уникальный идентификатор транзакции в виде {@link UUID} или {@code null}, если
     * идентификатор недопустим или не соответствует уникальному идентификатору аккаунта.
     */
    private static UUID readAndValidateUniqueNumber(String number, Account account) {
        UUID uniqueNumber;

        try {
            uniqueNumber = UUID.fromString(number);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid transaction ID");
            return null;
        }

        if (!account.getUniqueNumber().equals(uniqueNumber)) {
            System.out.println("Invalid transaction ID");
            return null;
        }

        return uniqueNumber;
    }


}
