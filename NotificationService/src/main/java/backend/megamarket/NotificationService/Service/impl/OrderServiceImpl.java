package backend.megamarket.NotificationService.Service.impl;

import backend.megamarket.NotificationService.Service.OrderService;
import backend.megamarket.NotificationService.Service.dto.OrderDto;
import backend.megamarket.NotificationService.Service.messaging.event.OrderEvent;
import backend.megamarket.NotificationService.db.dao.OrderRepository;
import backend.megamarket.NotificationService.db.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl  implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${topic.add-order}")
    private String orderConfirmationTopic;

    @Override
    @Transactional
    public List<Order> save(List<OrderEvent> clientDto) {
        try {
            List<Order> orders = clientDto.stream().map(p -> {
                Order newOrder = new Order();
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

    @Override
    public List<Order> getAllProducts() {
        return orderRepository.findAll();
    }
    @Override
    public List<Order> getOrdersByOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId);
    }
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
