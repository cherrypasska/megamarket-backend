package backend.megamarket.service.orderservice.services;

import backend.megamarket.service.orderservice.dtos.OrderItemDto;

import java.util.List;

public interface OrderService {

    void checkOrder(List<OrderItemDto> items);
}