package backend.megamarket.notificationservice.controller;

/**
 * Исключение, выбрасываемое при отсутствии заказов.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Заказ с ID " + id + " не найден.");
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}

