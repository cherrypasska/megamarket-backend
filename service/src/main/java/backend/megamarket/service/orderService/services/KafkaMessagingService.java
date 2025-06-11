package backend.megamarket.service.orderservice.services;

import backend.megamarket.service.orderservice.dtos.OrderSendEventDto;

import java.util.List;

public interface KafkaMessagingService {

    void sendOrder(List<OrderSendEventDto> orderSendEvent);
}
