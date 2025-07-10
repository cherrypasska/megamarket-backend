package backend.megamarket.orderservice.controller;

import backend.megamarket.orderservice.dto.ErrorResponseDto;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

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
     * @return HTTP 400 (Bad Request) с сообщением об ошибке в формате {@link ErrorResponseDto}
     */
    @ExceptionHandler(EmptyOrderException.class)
    public ResponseEntity<ErrorResponseDto> handleEmptyOrder(EmptyOrderException ex) {
        return ResponseEntity.badRequest().body(buildErrorResponse("Order Service", ex.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link InsufficientQuantityException}, возникающее при нехватке товара на складе.
     *
     * @param ex исключение {@link InsufficientQuantityException}
     * @return HTTP 400 (Bad Request) с сообщением об ошибке в формате {@link ErrorResponseDto}
     */
    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficient(InsufficientQuantityException ex) {
        return ResponseEntity.badRequest().body(buildErrorResponse("Inventory Service", ex.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link UserNotFoundException}, возникающее при попытке обращения к несуществующему пользователю.
     *
     * @param ex исключение {@link UserNotFoundException}
     * @return HTTP 404 (Not Found) с сообщением об ошибке в формате {@link ErrorResponseDto}
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse("User Service", ex.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link NotificationException}, возникающее при сбое отправки уведомления.
     *
     * @param ex исключение {@link NotificationException}
     * @return HTTP 500 (Internal Server Error) с сообщением об ошибке в формате {@link ErrorResponseDto}
     */
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ErrorResponseDto> handleNotification(NotificationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse("Notification Service", ex.getMessage()));
    }

    /**
     * Обрабатывает все остальные неперехваченные исключения.
     *
     * @param ex любое неперехваченное исключение
     * @return HTTP 500 (Internal Server Error) с общим сообщением в формате {@link ErrorResponseDto}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse("Unknown", ex.getMessage()));
    }

    /**
     * Формирует DTO с информацией об ошибке.
     *
     * @param service имя сервиса, в котором возникло исключение
     * @param message сообщение об ошибке
     * @return объект {@link ErrorResponseDto}
     */
    private ErrorResponseDto buildErrorResponse(String service, String message) {
        var er = ErrorResponseDto.builder()
                .time(Instant.now().toString())
                .service(service)
                .userId(MDC.get("userId"))
                .orderId(MDC.get("orderId"))
                .message(message)
                .build();
        MDC.clear();
        return er;
    }
}
