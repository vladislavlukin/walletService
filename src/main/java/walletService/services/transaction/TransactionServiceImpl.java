package walletService.services.transaction;

import lombok.AllArgsConstructor;
import walletService.data.Account;
import walletService.dto.TransactionType;
import walletService.data.Transactional;
import walletService.repositories.AccountRepository;
import walletService.repositories.TransactionalRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Реализация интерфейса {@code TransactionService} для управления операциями с транзакциями и историей транзакций.
 */
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionalRepository transactionalRepository;
    private final Account account;

    @Override
    public String executeTransaction(String amount, TransactionType transactionType) {
        String text = "Operation completed";
        try {
            performTransaction(amount, transactionType);
        }catch (IllegalArgumentException e){
            text = e.getMessage();
        }
        return text;
    }

    /**
     * Выполняет транзакцию, обрабатывая сумму и тип.
     *
     * @param amount         Сумма транзакции в долларах.
     * @param transactionType Тип транзакции (DEBIT или CREDIT).
     */
    private void performTransaction(String amount, TransactionType transactionType) {
        long centsAmount = convertDollarsToCents(amount);

        if (account.getIsDeleted() || account.getIsBlocked()) {
            throw new IllegalArgumentException("Account is not valid");
        }

        long balance = calculateBalance(transactionType, centsAmount);
        transactionalRepository.addTransactional(createNewTransaction(centsAmount, balance, transactionType));

    }

    /**
     * Вычисляет баланс после выполнения транзакции.
     *
     * @param transactionType Тип транзакции (DEBIT или CREDIT).
     * @param centsAmount     Сумма транзакции в центах.
     * @return Баланс после выполнения транзакции.
     */
    private long calculateBalance(TransactionType transactionType, long centsAmount) {
        long currentCentsBalance = account.getBalanceInCents();
        long balance;

        if (transactionType.equals(TransactionType.DEBIT) && currentCentsBalance >= centsAmount) {
            balance = currentCentsBalance - centsAmount;
        } else if (transactionType.equals(TransactionType.CREDIT)) {
            balance = currentCentsBalance + centsAmount;
        } else {
            throw new IllegalArgumentException("Requested amount exceeds the current balance");
        }
        return balance;
    }

    /**
     * Создает новую транзакцию и изменяет баланс в аккаунте пользователя.
     *
     * @param amount         Сумма транзакции в центах.
     * @param balance        Баланс после выполнения транзакции.
     * @param transactionType Тип транзакции (DEBIT или CREDIT).
     * @return Созданный объект Transactional.
     */
    private Transactional createNewTransaction(long amount, long balance, TransactionType transactionType) {
        Account updateAccount = accountRepository.updateAccountByAmount(account, balance);

        return Transactional.builder()
                .transactionDate(getCurrentLocalDateTime())
                .isBlocked(false)
                .amount(amount)
                .balance(balance)
                .transactionNumber(UUID.randomUUID())
                .transactionType(transactionType)
                .account(updateAccount)
                .build();
    }

    /**
     * Конвертирует сумму в долларах в центы.
     *
     * @param dollarAmount Сумма транзакции в долларах (в виде строки).
     * @return Сумма транзакции в центах.
     * @throws IllegalArgumentException Если формат суммы недопустим.
     */
    private static long convertDollarsToCents(String dollarAmount) {
        try {
            BigDecimal dollars = new BigDecimal(dollarAmount);
            BigDecimal cents = dollars.multiply(new BigDecimal("100"));
            cents = cents.setScale(0, RoundingMode.HALF_UP);
            return cents.longValueExact();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid dollar amount format: " + dollarAmount);
        }
    }

    /**
     * Получает текущее локальное время и дату в формате "yyyy-MM-dd HH:mm:ss".
     *
     * @return Строка с текущим временем и датой.
     */
    private static String getCurrentLocalDateTime() {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime currentDateTime = LocalDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    @Override
    public String viewTransactionHistory() {
        String text;
        try {
            text = viewTransactions();
        }catch (IllegalArgumentException e){
            text = e.getMessage();
        }
        return text;
    }

    /**
     * Отображает историю транзакций в виде текста.
     *
     * @return Строка с историей транзакций в текстовом формате.
     */
    private String viewTransactions() {
        StringBuilder result = new StringBuilder();

        List<Transactional> transactions = transactionalRepository.getTransactionalByAccount(account);

        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("You don't have transaction");
        }

        for (Transactional transaction : transactions) {
            result.append("Transaction Number: ").append(transaction.getTransactionNumber()).append("\n");
            result.append("Date: ").append(transaction.getTransactionDate()).append("\n");
            result.append("Type: ").append(transaction.getTransactionType()).append("\n");
            result.append("Amount: ").append((double) transaction.getAmount() / 100).append("\n");
            result.append("Balance: ").append((double) transaction.getBalance() / 100).append("\n\n");
        }


        return result.toString();
    }

}
