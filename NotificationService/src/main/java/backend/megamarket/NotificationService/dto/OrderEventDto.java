package backend.megamarket.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
     * Уникальный идентификатор пользователя (заказчика)
     */
    private Long orderId;

    /**
     * Уникальный идентификатор заказа
     */
    private Long userId;

    /**
     * Продукты (вещи) входящие в заказ
     */
    private List<OrderItemDto> products;
}