package backend.megamarket.notificationservice.service;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для обработки сообщений из Kafka, связанных с созданием заказов.
 * Получает сообщения и сохраняет заказы через {@link OrderService}.
 */
@Slf4j
@Service
@AllArgsConstructor
public class KafkaMessagingServiceImpl {

    /**
     * Топик Kafka.
     */
    private static final String topicCreateOrder = "${topic.send-order}";

    /**
     * Идентификатор группы Kafka-консюмера.
     */
    private static final String kafkaConsumerGroupId = "${spring.kafka.consumer.group-id}";

    private final OrderService orderService;

    private final KafkaProducerService kafkaProducer;

    /**
     * Обрабатывает сообщения о создании заказов из Kafka.
     * Полученный список DTO заказов преобразуется и сохраняется.
     *
     * @param orderEvent список DTO заказов {@link OrderEventDto} из сообщения Kafka
     * @return возвращает список заказов после обработки
     */
    @Transactional
    @KafkaListener(topics = topicCreateOrder, groupId = kafkaConsumerGroupId)
    public OrderEventDto createOrder(OrderEventDto orderEvent) {
        log.info("заказ {} прибыл в notification service", orderEvent);
        orderService.save(orderEvent);
        kafkaProducer.sendOrderConfirmation(orderEvent);

        return orderEvent;
    }
}