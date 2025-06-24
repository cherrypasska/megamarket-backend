package backend.megamarket.service.orderservice.service;

import backend.megamarket.service.orderservice.dto.OrderSendEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для отправки сообщений заказов в Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessagingServiceImpl implements KafkaMessagingService {
    /**
     * Топик Kafka для отправки сообщений о заказах.
     */
    @Value("${topic.send-order}")
    private String sendClientTopic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Отправляет список событий заказа в Kafka топик.
     *
     * @param orderSendEvent список DTO с информацией о заказах для отправки
     */
    @Override
    public void sendOrder(List<OrderSendEventDto> orderSendEvent) {
        kafkaTemplate.send(sendClientTopic, orderSendEvent);
    }
}