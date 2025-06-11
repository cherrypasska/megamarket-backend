package backend.megamarket.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO данных заказа.
 * Содержит информацию о продукте, количестве, цене, скидке и идентификаторе пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEventDto {

    /**
     * Идентификатор заказа.
     */
    private Long orderId;

    /**
     * Идентификатор продукта.
     */
    private Long productId;

    /**
     * Количество продукта в заказе.
     */
    private Long quantity;

    /**
     * Цена продукта за 1 штуку.
     */
    private Double price;

    /**
     * Скидка неа продукт.
     */
    private Double sale;

    /**
     * Идентификатор пользователя.
     */
    private Long userId;
}