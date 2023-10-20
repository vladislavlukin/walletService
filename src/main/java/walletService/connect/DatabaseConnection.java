package walletService.connect;

import walletService.connect.config.DatabaseConfig;
import walletService.exceptions.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Утилитарный класс для установления соединения с базой данных.
 */
public class DatabaseConnection {
    /**
     * Устанавливает соединение с базой данных на основе предоставленной конфигурации.
     *
     * @param config Конфигурация базы данных.
     * @return Объект Connection, представляющий установленное соединение с базой данных.
     * @throws DatabaseConnectionException Если произошла ошибка при установлении соединения.
     */
    public static Connection getConnection(DatabaseConfig config) throws DatabaseConnectionException {
        try {
            Class.forName(config.getDriver());
            return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseConnectionException("Failed to database connection.");
        }
    }

    /**
     * Закрывает соединение с базой данных.
     *
     * @param connection Соединение, которое требуется закрыть.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
