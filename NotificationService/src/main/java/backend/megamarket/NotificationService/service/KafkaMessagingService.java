package backend.megamarket.notificationservice.service;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface KafkaMessagingService {
    @Transactional
    @KafkaListener(topics = "${topic.send-order}", groupId = "${spring.kafka.consumer.group-id}")
    List<OrderEventDto> createOrder(List<OrderEventDto> orderEvent);
}
