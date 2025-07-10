package backend.megamarket.notificationservice.dto;

import backend.megamarket.notificationservice.entity.OrderEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class OrderProductDto {

    /**
     * Идентификатор продукта, включенного в заказ.
     */
    private Long productId;

    /**
     * Количество единиц данного продукта в заказе.
     */
    private Long quantity;

    /**
     * Цена за единицу продукта на момент заказа.
     */
    private Double price;

    /**
     * Скидка на продукт.
     */
    private Double sale;
}
