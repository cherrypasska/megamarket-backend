package backend.megamarket.notificationservice.service;

import backend.megamarket.notificationservice.entity.OrderEntity;
import backend.megamarket.notificationservice.dto.OrderEventDto;

import java.util.List;

public interface OrderService {
    List<OrderEntity> getAllProducts();

    List<OrderEntity> getOrdersByOrderId(Long orderId);

    List<OrderEntity> getOrdersByUserId(Long userId);

    List<OrderEntity> save(List<OrderEventDto> clientDto);
}