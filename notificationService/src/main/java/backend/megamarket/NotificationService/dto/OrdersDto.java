package backend.megamarket.notificationservice.dto;

import backend.megamarket.notificationservice.entity.OrderProductsEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto {

    /**
     * Уникальный идентификатор заказа
     */
    private Long orderId;

    /**
     * Полная стоимость заказа
     */
    private Double totalPrice;

    /**
     * Уникальный идентификатор пользователя (заказчика)
     */
    private Long userId;

    /**
     * Продукты входящие в заказ
     */
    private List<OrderProductDto> products;
}
