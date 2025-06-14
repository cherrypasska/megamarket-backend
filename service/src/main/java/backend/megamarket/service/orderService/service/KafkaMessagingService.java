package backend.megamarket.service.orderservice.service;

import backend.megamarket.service.orderservice.dto.OrderSendEventDto;

import java.util.List;

public interface KafkaMessagingService {

    void sendOrder(List<OrderSendEventDto> orderSendEvent);
}
