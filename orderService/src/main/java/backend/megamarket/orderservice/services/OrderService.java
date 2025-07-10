package backend.megamarket.orderservice.services;

import backend.megamarket.orderservice.dto.OrderItemDto;

import java.util.List;

public interface OrderService {

    void checkOrder(List<OrderItemDto> items);
}