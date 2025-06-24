package backend.megamarket.service.orderservice.service;

import backend.megamarket.service.orderservice.dto.OrderItemDto;

import java.util.List;

public interface OrderService {

    void checkOrder(List<OrderItemDto> items);
}