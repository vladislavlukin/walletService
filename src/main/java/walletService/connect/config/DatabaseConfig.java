package walletService.connect.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Этот класс представляет конфигурацию базы данных, которую можно загрузить из файла конфигурации в формате YAML.
 * Он предоставляет методы для загрузки параметров подключения к базе данных и пути к файлу изменений Liquibase.
 */
@Getter
@RequiredArgsConstructor
public class DatabaseConfig {
    private final String configFile;
    private String driver;
    private String url;
    private String username;
    private String password;

    /**
     * Загружает конфигурацию базы данных из указанного файла конфигурации в формате YAML.
     */
    public void loadConfig() {
        Yaml yaml = new Yaml();
        try (FileInputStream configStream = new FileInputStream(configFile)) {
            Map<String, Map<String, String>> data = yaml.load(configStream);
            Map<String, String> databaseConfig = data.get("datasource");
            this.driver = databaseConfig.get("driverClassName");
            this.url = databaseConfig.get("url");
            this.username = databaseConfig.get("username");
            this.password = databaseConfig.get("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Загружает путь к файлу изменений Liquibase из указанного файла конфигурации в формате YAML.
     *
     * @return Путь к файлу изменений Liquibase или null, если произошла ошибка.
     */
    public String loadChangelogPath() {
        Yaml yaml = new Yaml();
        try (FileInputStream configStream = new FileInputStream(configFile)) {
            Map<String, Map<String, String>> config = yaml.load(configStream);
            return config.get("liquibase").get("change-log");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

