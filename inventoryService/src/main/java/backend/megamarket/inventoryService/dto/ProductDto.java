package backend.megamarket.inventoryservice.dto;

import lombok.Data;

@Data
public class ProductDto {
    /**
     * Название продукта (уникальное и обязательное).
     */
    private String name;

    /**
     * Количество продукта на складе.
     */
    private Long quantity;

    /**
     * Цена продукта.
     */
    private Double price;

    /**
     * Скидка на продукт.
     */
    private Double sale;
}
