package backend.megamarket.inventoryservice.controller;

/**
 * Исключение, выбрасываемое, если продукт не найден.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Продукт с ID " + id + " не найден.");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
