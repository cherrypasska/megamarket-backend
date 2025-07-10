package backend.megamarket.notificationservice.service;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Топик Kafka.
     */
    @Value("${topic.add-order}")
    private String orderConfirmationTopic;

    public void sendOrderConfirmation(OrderEventDto orderEventDto) {
        kafkaTemplate.send(orderConfirmationTopic, orderEventDto);
        log.info("Заказ с id {} отправлен в Order Service", orderEventDto.getOrderId());
    }
}
