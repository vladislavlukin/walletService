package walletService.repositories;

import walletService.data.Account;
import walletService.data.Transactional;

import java.util.List;

/**
 * Интерфейс {@code TransactionalRepository} представляет собой репозиторий для управления транзакциями.
 * Он содержит методы для получения транзакций, связанных с определенным аккаунтом, и добавления новых транзакций.
 */
public interface TransactionalRepository {
    /**
     * Получает список транзакций для указанного аккаунта.
     *
     * @param account Аккаунт, для которого нужно получить транзакции.
     * @return Список транзакций, связанных с указанным аккаунтом.
     */
    List<Transactional> getTransactionalByAccount(Account account);

    /**
     * Добавляет новую транзакцию в репозиторий.
     *
     * @param transactional Новая транзакция для добавления.
     */
    void addTransactional(Transactional transactional);
}
