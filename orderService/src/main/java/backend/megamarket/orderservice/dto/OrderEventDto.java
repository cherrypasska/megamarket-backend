package backend.megamarket.orderservice.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO, представляющий событие заказа, используемое для передачи данных между микросервисами.
 * <p>
 * Содержит информацию о заказе, включая его ID, ID пользователя и список заказанных продуктов.
 */
@Data
public class OrderEventDto {

    /**
     * Уникальный идентификатор заказа.
     */
    private Long orderId;

    /**
     * Идентификатор пользователя, оформившего заказ.
     */
    private Long userId;

    /**
     * Список DTO, описывающих товары в заказе.
     */
    private List<OrderSendEventDto> products;
}