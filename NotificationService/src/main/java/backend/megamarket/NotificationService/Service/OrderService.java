package backend.megamarket.NotificationService.Service;

import backend.megamarket.NotificationService.Service.dto.OrderDto;
import backend.megamarket.NotificationService.Service.messaging.event.OrderEvent;
import backend.megamarket.NotificationService.db.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllProducts();

    List<Order> getOrdersByOrderId(Long orderId);

    List<Order> getOrdersByUserId(Long userId);

    List<Order> save(List<OrderEvent> clientDto);
}