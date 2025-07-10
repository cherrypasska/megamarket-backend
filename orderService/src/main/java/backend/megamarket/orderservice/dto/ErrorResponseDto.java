package backend.megamarket.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для представления информации об ошибке, возвращаемой клиенту.
 * <p>
 * Содержит подробные сведения, включая временную метку, идентификаторы пользователя и заказа,
 * сообщение об ошибке и источник ошибки.
 */
@Data
@Builder
@AllArgsConstructor
public class ErrorResponseDto {

    /**
     * Временная метка возникновения ошибки в формате ISO-8601.
     * Например: {@code 2025-07-09T14:35:22Z}
     */
    private String time;

    /**
     * Название сервиса, в котором произошла ошибка.
     * Например: {@code OrderService} или {@code NotificationService}.
     */
    private String service;

    /**
     * Идентификатор пользователя, для которого возникла ошибка.
     * Может быть {@code null}, если ошибка не связана с конкретным пользователем.
     */
    private String userId;

    /**
     * Идентификатор заказа, связанного с ошибкой.
     * Может быть {@code null}, если ошибка не связана с конкретным заказом.
     */
    private String orderId;

    /**
     * Описание ошибки, предназначенное для клиента.
     */
    private String message;
}
