package backend.megamarket.service.orderservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи информации о позиции в заказе.
 * <p>
 * Содержит идентификатор товара и количество, которое пользователь хочет заказать.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    /**
     * Уникальный идентификатор товара.
     */
    private Long productId;

    /**
     * Количество товара для заказа.
     */
    private Long quantity;
}
