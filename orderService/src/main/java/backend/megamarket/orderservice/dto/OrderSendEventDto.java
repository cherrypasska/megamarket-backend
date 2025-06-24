package backend.megamarket.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для события отправки заказа.
 * <p>
 * Используется для передачи информации о заказе при его создании или обработке,
 * например, при отправке события в систему уведомлений или в очередь сообщений.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSendEventDto {

    /**
     * Уникальный идентификатор заказа.
     */
    //private Long orderId;

    /**
     * Идентификатор пользователя, который сделал заказ.
     */
    //private Long userId;

    /**
     * Идентификатор товара, включенного в заказ.
     */
    private Long productId;

    /**
     * Количество заказанного товара.
     */
    private Long quantity;

    /**
     * Цена за единицу товара на момент заказа.
     */
    private Double price;

    /**
     * Размер скидки, применённой к товару (в денежном выражении).
     */
    private Double sale;
}
