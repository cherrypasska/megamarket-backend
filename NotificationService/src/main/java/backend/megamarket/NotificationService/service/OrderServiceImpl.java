package backend.megamarket.notificationservice.service;

import backend.megamarket.notificationservice.repository.OrderRepository;
import backend.megamarket.notificationservice.entity.OrderEntity;
import backend.megamarket.notificationservice.dto.OrderEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация сервиса управления заказами.
 * Обрабатывает сохранение заказов, получение заказов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl  implements OrderService {

    private final OrderRepository orderRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Топик Kafka.
     */
    @Value("${topic.add-order}")
    private String orderConfirmationTopic;

    /**
     * Сохраняет список заказов в базе данных и отправляет подтверждение в Kafka.
     *
     * @param clientDto список DTO заказов {@link OrderEventDto} для сохранения
     * @return список сохранённых сущностей заказов {@link OrderEntity}, или null в случае ошибки
     */
    @Override
    @Transactional
    public List<OrderEntity> save(List<OrderEventDto> clientDto) {
        try {
            List<OrderEntity> orders = clientDto.stream().map(p -> {
                OrderEntity newOrder = new OrderEntity();
                newOrder.setOrderId(p.getOrderId());
                newOrder.setPrice(p.getPrice());
                newOrder.setQuantity(p.getQuantity());
                newOrder.setSale(p.getSale());
                newOrder.setProductId(p.getProductId());
                newOrder.setUserId(p.getUserId());
                newOrder.setTotalPrice(p.getQuantity() * (p.getPrice() - p.getPrice() * p.getSale()));
                return newOrder;
            }).toList();
            orderRepository.saveAll(orders);
            log.info("Save order");
            kafkaTemplate.send(orderConfirmationTopic, clientDto);
            return orders;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * Получает список всех заказов из базы данных.
     *
     * @return список всех заказов {@link OrderEntity}
     */
    @Override
    public List<OrderEntity> getAllProducts() {
        return orderRepository.findAll();
    }

    /**
     * Получает список заказов по идентификатору заказа.
     *
     * @param orderId идентификатор заказа
     * @return список заказов с указанным идентификатором {@link OrderEntity}
     */
    @Override
    public List<OrderEntity> getOrdersByOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    /**
     * Получает список заказов по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список заказов, принадлежащих указанному пользователю {@link OrderEntity}
     */
    @Override
    public List<OrderEntity> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
