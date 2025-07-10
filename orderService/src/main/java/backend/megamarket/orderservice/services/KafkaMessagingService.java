package backend.megamarket.orderservice.services;

import backend.megamarket.orderservice.dto.OrderEventDto;

public interface KafkaMessagingService {

    void sendOrder(OrderEventDto orderSendEvent);
}
