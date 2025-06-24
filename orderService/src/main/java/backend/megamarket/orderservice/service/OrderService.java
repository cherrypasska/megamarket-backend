package backend.megamarket.orderservice.service;

import backend.megamarket.orderservice.dto.OrderItemDto;

import java.util.List;

public interface OrderService {

    void checkOrder(List<OrderItemDto> items);
}