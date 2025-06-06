package backend.megamarket.service.orderService.db.dto;

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