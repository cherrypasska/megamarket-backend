package backend.megamarket.service.orderService.dbs.dao;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSendEvent {
    private Long userId;
    private Long productId;
    private Long quantity;
    private Double price;
    private Double sale;
}