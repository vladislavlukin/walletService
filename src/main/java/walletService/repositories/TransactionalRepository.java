package walletService.repositories;

import walletService.data.Account;
import walletService.data.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс {@code TransactionalRepository} представляет собой репозиторий для управления транзакциями.
 * Он содержит методы для получения транзакций, связанных с определенным аккаунтом, и добавления новых транзакций.
 */
public class TransactionalRepository {
    private final List<Transactional> transactionalList = new ArrayList<>();

    /**
     * Получает список транзакций для указанного аккаунта.
     *
     * @param account Аккаунт, для которого нужно получить транзакции.
     * @return Список транзакций, связанных с указанным аккаунтом.
     */
    public List<Transactional> getTransactionalByAccount(Account account) {
        return transactionalList
                .stream()
                .filter(transactional -> transactional.getAccount().equals(account))
                .collect(Collectors.toList());
    }

    /**
     * Добавляет новую транзакцию в репозиторий.
     *
     * @param transactional Новая транзакция для добавления.
     */
    public void addTransactional(Transactional transactional) {
        transactionalList.add(transactional);

    }
}
