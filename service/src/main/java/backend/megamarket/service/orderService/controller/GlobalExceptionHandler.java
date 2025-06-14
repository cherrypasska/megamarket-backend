package backend.megamarket.service.orderservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * <p>
 * Перехватывает исключения во время выполнения и возвращает
 * соответствующий HTTP-ответ с сообщением об ошибке.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение {@link EmptyOrderException}, возникающее при попытке оформить пустой заказ.
     *
     * @param ex исключение {@link EmptyOrderException}
     * @return HTTP 400 (Bad Request) с сообщением об ошибке
     */
    @ExceptionHandler(EmptyOrderException.class)
    public ResponseEntity<String> handleEmptyOrder(EmptyOrderException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    /**
     * Обрабатывает исключение {@link InsufficientQuantityException}, возникающее при нехватке товара на складе.
     *
     * @param ex исключение {@link InsufficientQuantityException}
     * @return HTTP 409 (Conflict) с сообщением об ошибке
     */
    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<String> handleInsufficientQuantity(InsufficientQuantityException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    /**
     * Обрабатывает все остальные необработанные исключения.
     *
     * @param ex любое неперехваченное исключение
     * @return HTTP 500 (Internal Server Error) с общим сообщением
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла внутренняя ошибка сервера");
    }
}