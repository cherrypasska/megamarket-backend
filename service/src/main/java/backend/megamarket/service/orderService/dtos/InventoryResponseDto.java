package backend.megamarket.service.orderservice.dtos;

import lombok.Data;

import java.util.List;

/**
 * DTO для ответа, содержащего информацию о товарах на складе.
 */
@Data
public class InventoryResponseDto {

    /**
     * Список товаров на складе.
     */
    private List<ProductDTO> items;

    /**
     * DTO для представления информации о конкретном товаре.
     */
    @Data
    public static class ProductDTO {

        /**
         * Уникальный идентификатор товара.
         */
        private Long productId;

        /**
         * Название товара.
         */
        private String name;

        /**
         * Цена товара.
         */
        private Double price;

        /**
         * Размер скидки на товар (в денежном выражении или в процентах, зависит от контекста).
         */
        private Double discount;

        /**
         * Количество товара, доступного на складе.
         */
        private Long quantity;
    }
}
