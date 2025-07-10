package backend.megamarket.orderservice.controller;

/**
 * Исключение, выбрасываемое при попытке обработки пустого заказа.
 * <p>
 * Может использоваться, например, при проверке содержимого корзины перед оформлением заказа.
 */
public class EmptyOrderException extends RuntimeException {

    /**
     * Создаёт исключение с заданным сообщением.
     *
     * @param message сообщение, описывающее причину возникновения исключения
     */
    public EmptyOrderException(String message) {
        super(message);
    }
}