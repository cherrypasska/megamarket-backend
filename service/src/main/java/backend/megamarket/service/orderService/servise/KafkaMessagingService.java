package backend.megamarket.service.orderService.servise;

import backend.megamarket.service.orderService.dbs.dao.OrderSendEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessagingService {
    @Value("${topic.send-order}")
    private String sendClientTopic;
    private final KafkaTemplate<String , Object> kafkaTemplate;
    public void sendOrder(List<OrderSendEvent> orderSendEvent) {
        kafkaTemplate.send(sendClientTopic, orderSendEvent);
    }

    /*private static final String topicCreateOrder = "${topic.add-order}";
    private static final String kafkaConsumerGroupId = "${spring.kafka.consumer.group-id}";
    private final ModelMapper modelMapper;
    @Transactional
    @KafkaListener(topics = topicCreateOrder, groupId = kafkaConsumerGroupId)
    public List<OrderSendEvent> createOrder(List<OrderSendEvent> orderEvent) {
        log.info("Заказ создан");

        return orderEvent;
    }*/
}