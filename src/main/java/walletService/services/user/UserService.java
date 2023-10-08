package walletService.services.user;

import walletService.dto.UserResponse;

/**
 * Интерфейс, представляющий сервис для управления операциями пользователя и получения ответов.
 */
public interface UserService {

    /**
     * Получает ответ от сервиса пользователя.
     *
     * @return Объект типа UserResponse, представляющий ответ пользователя.
     */
    UserResponse getResponse();
}
