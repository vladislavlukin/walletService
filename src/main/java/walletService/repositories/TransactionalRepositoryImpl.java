package walletService.repositories;

import lombok.AllArgsConstructor;
import walletService.data.Account;
import walletService.data.Transactional;
import walletService.dto.TransactionType;
import walletService.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Реализация интерфейса  {@code TransactionalRepository}
 * */
@AllArgsConstructor
public class TransactionalRepositoryImpl implements TransactionalRepository {
    private Connection connection;

    @Override
    public List<Transactional> getTransactionalByAccount(Account account) throws DatabaseException {
        try {
            beginTransaction();
            List<Transactional> transactionalList = new ArrayList<>();
            String sql = "SELECT * FROM wallet.transactional WHERE account_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, account.getId());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Transactional transactional = Transactional.builder()
                            .account(account)
                            .transactionNumber((UUID) resultSet.getObject("transaction_number"))
                            .amount(resultSet.getLong("amount"))
                            .balance(resultSet.getLong("balance"))
                            .transactionType(TransactionType.valueOf(resultSet.getString("transaction_type")))
                            .isBlocked(resultSet.getBoolean("is_blocked"))
                            .transactionDate(resultSet.getTimestamp("transaction_date").toLocalDateTime())
                            .build();

                    transactionalList.add(transactional);
                }
                commitTransaction();
                return transactionalList;
            }
        } catch (SQLException e) {
            String errorText = "Failed to get transactional by account.";
            rollbackTransaction(errorText);
            throw new DatabaseException(errorText);
        }
    }

    @Override
    public void addTransactional(Transactional transactional) throws DatabaseException {
        try {
            beginTransaction();
            String sql = "INSERT INTO wallet.transactional (account_id, transaction_number, amount, balance, transaction_type, is_blocked, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, transactional.getAccount().getId());
                statement.setObject(2, transactional.getTransactionNumber());
                statement.setLong(3, transactional.getAmount());
                statement.setLong(4, transactional.getBalance());
                statement.setString(5, transactional.getTransactionType().toString());
                statement.setBoolean(6, transactional.getIsBlocked());
                statement.setTimestamp(7, Timestamp.valueOf(transactional.getTransactionDate()));

                statement.executeUpdate();
            }
            commitTransaction();
        } catch (SQLException e) {
            String errorText = "Failed to add a transactional.";
            rollbackTransaction(errorText);
            throw new DatabaseException(errorText);
        }
    }

    private void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }

    private void commitTransaction() throws SQLException {
        connection.commit();
    }

    private void rollbackTransaction(String errorText) throws DatabaseException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new DatabaseException(errorText + "\nError rolling back the transaction. Failed to rollback changes in the database.");
        }
    }
}
