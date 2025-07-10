package backend.megamarket.notificationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Обработчик исключений, специфичный для {@link OrderController}.
 */
@RestControllerAdvice(assignableTypes = OrderController.class)
public class OrderControllerExceptionHandler {

    /**
     * Обработка случая, когда заказ не найден.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    /**
     * Обработка всех прочих исключений.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла непредвиденная ошибка.");
    }
}
