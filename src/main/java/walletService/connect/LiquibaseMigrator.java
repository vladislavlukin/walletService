package walletService.connect;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import lombok.AllArgsConstructor;
import walletService.exceptions.LiquibaseException;

import java.sql.Connection;

/**
 * Класс LiquibaseMigrator отвечает за применение миграций базы данных с использованием Liquibase.
 */
@AllArgsConstructor
public class LiquibaseMigrator {
    private final Connection connection;

    /**
     * Применяет миграции базы данных, используя указанный файл changelog.
     *
     * @param changelogPath Путь к файлу changelog Liquibase.
     * @throws LiquibaseException В случае возникновения ошибки в процессе миграции.
     */
    public void migrate(String changelogPath) throws LiquibaseException {
        try {
            Database database = getDatabase();
            ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
            Liquibase liquibase = new Liquibase(changelogPath, resourceAccessor, database);
            liquibase.update();
        } catch (Exception e) {
            throw new LiquibaseException("Failed to apply database migrations");
        }
    }

    /**
     * Получает объект базы данных на основе предоставленного соединения.
     *
     * @return Объект базы данных Liquibase.
     * @throws Exception В случае ошибки при получении объекта базы данных.
     */
    private Database getDatabase() throws Exception {
        return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
    }
}

