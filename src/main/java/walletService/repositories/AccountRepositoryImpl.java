package walletService.repositories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import walletService.data.Account;
import walletService.exceptions.DatabaseException;

import java.sql.*;
import java.util.*;

/**
 * Реализация интерфейса {@code AccountRepository}
 * */
@AllArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {
    private Connection connection;

    @Override
    public List<Account> getAccounts() throws DatabaseException {
        try {
            beginTransaction();
            List<Account> accounts = new ArrayList<>();
            String sql = "SELECT * FROM wallet.account";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    accounts.add(mapResultSetToAccount(resultSet));
                }
                commitTransaction();
            }
            return accounts;
        } catch (SQLException e) {
            String errorText = "Failed to retrieve accounts from the database.";
            rollbackTransaction(errorText);
            throw new DatabaseException(errorText);
        }
    }

    @Override
    public Account getAccountByLoginAndPassword(String login, String password) throws DatabaseException {
        try {
            beginTransaction();
            String sql = "SELECT * FROM wallet.account WHERE login = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, login);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return mapResultSetToAccount(resultSet);
                }
                commitTransaction();
            }
            return null;
        } catch (SQLException e) {
            String errorText = "Failed to retrieve account by login and password from the database.";
            rollbackTransaction(errorText);
            throw new DatabaseException(errorText);
        }
    }

    @Override
    public Account getAccountByLogin(String login) throws DatabaseException {
        try {
            beginTransaction();
            String sql = "SELECT * FROM wallet.account WHERE login = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, login);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return mapResultSetToAccount(resultSet);
                }
                commitTransaction();
            }
            return null;
        } catch (SQLException e) {
            String errorText = "Failed to retrieve account by login from the database.";
            rollbackTransaction(errorText);
            throw new DatabaseException(errorText);
        }
    }

    @Override
    public Account updateAccountByAmount(Account account, long balance) throws DatabaseException {
        try {
            beginTransaction();
            String sql = "UPDATE wallet.account SET balance_in_cents = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, balance);
                statement.setLong(2, account.getId());
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 1) {
                    account.setBalanceInCents(balance);
                    return account;
                }
                commitTransaction();
            }
            return null;
        } catch (SQLException e) {
            String errorText = "Failed to update account balance.";
            rollbackTransaction(errorText);
            throw new DatabaseException(errorText);
        }
    }

    @Override
    public boolean isLoginAlreadyExists(String login) throws DatabaseException {
        try {
            beginTransaction();
            String sql = "SELECT COUNT(*) FROM wallet.account WHERE login = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, login);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
                commitTransaction();
            }
            return false;
        } catch (SQLException e) {
            String errorText = "Failed to check if login already exists.";
            rollbackTransaction(errorText);
            throw new DatabaseException(errorText);
        }
    }

    @Override
    public void addNewAccount(Account account) throws DatabaseException {
        try {
            beginTransaction();
            String sql = "INSERT INTO wallet.account (full_name, login, password, balance_in_cents, unique_number, is_deleted, is_blocked, account_creation_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, account.getFullName());
                statement.setString(2, account.getLogin());
                statement.setString(3, account.getPassword());
                statement.setLong(4, account.getBalanceInCents());
                statement.setObject(5, account.getUniqueNumber());
                statement.setBoolean(6, account.getIsDeleted());
                statement.setBoolean(7, account.getIsBlocked());
                statement.setTimestamp(8, Timestamp.valueOf(account.getAccountCreationDate()));
                statement.executeUpdate();
            }
            commitTransaction();
        } catch (SQLException e) {
            String errorText = "Failed to add a new account.";
            rollbackTransaction(errorText);
            throw new DatabaseException(errorText);
        }
    }


    private Account mapResultSetToAccount(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setId(resultSet.getLong("id"));
        account.setFullName(resultSet.getString("full_name"));
        account.setLogin(resultSet.getString("login"));
        account.setPassword(resultSet.getString("password"));
        account.setBalanceInCents(resultSet.getLong("balance_in_cents"));
        account.setUniqueNumber((UUID) resultSet.getObject("unique_number"));
        account.setIsDeleted(resultSet.getBoolean("is_deleted"));
        account.setIsBlocked(resultSet.getBoolean("is_blocked"));
        account.setAccountCreationDate(resultSet.getTimestamp("account_creation_date").toLocalDateTime());
        return account;
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
