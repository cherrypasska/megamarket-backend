package backend.megamarket.NotificationService.Service.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEvent {
    private Long productId;
    private Long quantity;
    private Double price;
    private Double sale;
    private Long userId;
}