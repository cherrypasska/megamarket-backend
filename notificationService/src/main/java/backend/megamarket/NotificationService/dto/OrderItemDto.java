package backend.megamarket.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto {

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
