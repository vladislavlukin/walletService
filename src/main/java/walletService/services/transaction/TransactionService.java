package walletService.services.transaction;

import walletService.dto.TransactionType;

/**
 * Интерфейс {@code TransactionService} определяет операции, связанные с транзакциями и историей транзакций.
 */
public interface TransactionService {
    /**
     * Выполняет транзакцию с указанной суммой и типом.
     *
     * @param amount         Сумма транзакции в долларах.
     * @param transactionType Тип транзакции (DEBIT или CREDIT).
     * @return Строка с результатом операции.
     */
    String executeTransaction(String amount, TransactionType transactionType);

    /**
     * Возвращает историю транзакций для текущего аккаунта.
     *
     * @return Строка с историей транзакций.
     */
    String viewTransactionHistory();
}

