package backend.megamarket.service.orderService.db.dto;

import lombok.Data;

import java.util.List;

@Data
public class InventoryResponseDTO {
    private List<ProductDTO> items;

    @Data
    public static class ProductDTO {
        private Long productId;
        private String name;
        private Double price;
        private Double discount;
        private Long quantity;
    }
}
