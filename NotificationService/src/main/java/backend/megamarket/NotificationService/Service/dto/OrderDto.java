package backend.megamarket.NotificationService.Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Long productId;
    private Long quantity;
    private Double price;
    private Double sale;
    private Long userId;
}
