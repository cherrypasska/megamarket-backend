package backend.megamarket.service.orderservice.service;

import org.springframework.kafka.annotation.KafkaListener;

public interface KafkaOrderConfirmationListener {

    @KafkaListener(topics = "${topic.add-order}", groupId = "${spring.kafka.consumer.group-id}")
    void onOrderConfirmed(String message);
}
