package walletService.connect;

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
     * @return Объект Connection, представляющий установленное соединение с базой данных.
     * @throws ClassNotFoundException Если класс драйвера базы данных не найден.
     * @throws SQLException           Если произошла ошибка при подключении к базе данных.
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        String driver = System.getenv("POSTGRES_DRIVER");
        String url = System.getenv("POSTGRES_URL");
        String username = System.getenv("POSTGRES_USER");
        String password = System.getenv("POSTGRES_PASSWORD");

        Class.forName(driver);

        return DriverManager.getConnection(url, username, password);
    }
}
