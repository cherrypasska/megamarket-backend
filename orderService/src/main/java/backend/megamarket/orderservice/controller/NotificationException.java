package backend.megamarket.orderservice.controller;

/**
 * Исключение, выбрасываемое при ошибках, связанных с отправкой уведомлений.
 * <p>
 * Может использоваться для обработки ошибок при взаимодействии с внешним
 * notification-сервисом, таких как недоступность, ошибки сериализации или
 * внутренние сбои.
 */
public class NotificationException extends RuntimeException {

    /**
     * Создаёт новое исключение с указанным сообщением.
     *
     * @param message описание ошибки
     */
    public NotificationException(String message) {
        super(message);
    }

    /**
     * Создаёт новое исключение с указанным сообщением и причиной.
     *
     * @param message описание ошибки
     * @param cause   причина, вызвавшая исключение
     */
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
