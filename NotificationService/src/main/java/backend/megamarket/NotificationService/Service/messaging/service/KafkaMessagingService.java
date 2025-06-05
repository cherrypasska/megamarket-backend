package backend.megamarket.NotificationService.Service.messaging.service;

import backend.megamarket.NotificationService.Service.OrderService;
import backend.megamarket.NotificationService.Service.dto.OrderDto;
import backend.megamarket.NotificationService.Service.messaging.event.OrderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaMessagingService {
    private static final String topicCreateOrder = "${topic.send-order}";
    private static final String kafkaConsumerGroupId = "${spring.kafka.consumer.group-id}";
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Transactional
    @KafkaListener(topics = topicCreateOrder, groupId = kafkaConsumerGroupId/*, properties = {
            "spring.json.value.default.type=java.util.List<backend.megamarket.NotificationService.Service.messaging.event.OrderEvent>"}*/)
    public List<OrderEvent> createOrder(List<OrderEvent> orderEvent) {
        log.info("Message consumed {}", orderEvent);
        orderService.save(modelMapper.map(orderEvent, List.class));
        return orderEvent;
    }
}