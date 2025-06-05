package backend.megamarket.service.orderService.dbs.dao;

import lombok.Data;

import java.util.List;

public class InventoryResponseDTO {
    private List<ProductDTO> items;

    @Data
    public static class ProductDTO {
        private long productId;
        private int quantity;
        private String status;
    }
}
