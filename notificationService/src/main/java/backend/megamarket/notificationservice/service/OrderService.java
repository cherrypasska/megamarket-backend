package backend.megamarket.notificationservice.service;

import backend.megamarket.notificationservice.dto.OrdersDto;
import backend.megamarket.notificationservice.entity.OrderEntity;
import backend.megamarket.notificationservice.dto.OrderEventDto;
import backend.megamarket.notificationservice.entity.OrderProductsEntity;

import java.util.List;

public interface OrderService {
    List<OrdersDto> getAllProducts();

    List<OrdersDto> getOrdersByOrderId(Long orderId);

    List<OrdersDto> getOrdersByUserId(Long userId);

    List<OrderProductsEntity> save(OrderEventDto clientDto);
}