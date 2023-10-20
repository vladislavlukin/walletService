package walletService.services.admin;

import walletService.dto.AdminResponse;
import walletService.exceptions.DatabaseException;

/**
 * Интерфейс для административных операций.
 */
public interface AdminService {
    /**
     * Получает административный ответ для указанного логина.
     *
     * @param login Логин аккаунта или пустая строка для получения отчета для всех аккаунтов.
     * @return Объект AdminResponse с текстовым отчетом и флагом результата.
     */
    AdminResponse getAdminResponse(String login) throws DatabaseException;
}
