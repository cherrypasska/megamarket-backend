package backend.megamarket.orderservice.service;

import backend.megamarket.orderservice.dto.OrderEventDto;
import backend.megamarket.orderservice.dto.OrderSendEventDto;

import java.util.List;

public interface KafkaMessagingService {

    void sendOrder(OrderEventDto orderSendEvent);
}
