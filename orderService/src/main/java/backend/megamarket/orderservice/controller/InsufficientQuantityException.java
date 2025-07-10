package backend.megamarket.orderservice.controller;

/**
 * Исключение, выбрасываемое при попытке оформить заказ с количеством товара,
 * превышающим доступный остаток на складе.
 * <p>
 * Используется для сигнализации об ошибке бизнес-логики, связанной с нехваткой товара.
 */
public class InsufficientQuantityException extends RuntimeException {

    /**
     * Создаёт исключение с заданным сообщением.
     *
     * @param message описание причины ошибки (например, "Недостаточное количество товара на складе")
     */
    public InsufficientQuantityException(String message) {
        super(message);
    }
}