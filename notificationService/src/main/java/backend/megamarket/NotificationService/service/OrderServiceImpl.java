package backend.megamarket.notificationservice.service;

import backend.megamarket.notificationservice.entity.OrderProductsEntity;
import backend.megamarket.notificationservice.mapper.OrderEntityMapper;
import backend.megamarket.notificationservice.mapper.OrderProductsEntityMapper;
import backend.megamarket.notificationservice.repository.OrderProductsRepository;
import backend.megamarket.notificationservice.repository.OrderRepository;
import backend.megamarket.notificationservice.entity.OrderEntity;
import backend.megamarket.notificationservice.dto.OrderEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final OrderProductsEntityMapper orderProductsEntityMapper;

    private final OrderEntityMapper orderEntityMapper;

    private final OrderProductsRepository orderProductsRepository;

    /**
     * Сохраняет список заказов в базе данных и отправляет подтверждение в Kafka.
     *
     * @param clientDto список DTO заказов {@link OrderEventDto} для сохранения
     * @return список сохранённых сущностей заказов {@link OrderEntity}, или null в случае ошибки
     */
    @Override
    @Transactional
    public List<OrderProductsEntity> save(OrderEventDto clientDto) {

        Long orderId = clientDto.getOrderId();
        Long userId = clientDto.getUserId();

        log.info("Получен заказ на сохранение: orderId={}, userId={}, товаров={}",
                orderId, userId, clientDto.getProducts().size());

        OrderEntity orderEntity = orderEntityMapper.orderEntityMapping(clientDto);
        try {
            List<OrderProductsEntity> orders = clientDto.getProducts().stream().map(p -> {
                OrderProductsEntity newOrder = orderProductsEntityMapper.OrderProductsEntityMapping(clientDto, p);
                orderEntity.setTotalPrice(orderEntity.getTotalPrice() + p.getQuantity() * (p.getPrice() - p.getPrice() * p.getSale()));
                log.debug("Добавлен продукт в заказ: productId={}, quantity={}, price={}, sale={}",
                        p.getProductId(), p.getQuantity(), p.getPrice(), p.getSale());
                return newOrder;
            }).toList();
            orderRepository.save(orderEntity);
            orderProductsRepository.saveAll(orders);
            log.info("Заказ успешно сохранён: orderId={}, userId={}, итого сумма={}",
                    orderId, userId, orderEntity.getTotalPrice());
            return orders;
        }
        catch (Exception e) {
            log.error("Ошибка при сохранении заказа orderId={}, userId={}: {}", orderId, userId, e.getMessage(), e);
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
        List<OrderEntity> allOrders = orderRepository.findAll();
        log.info("Получены все заказы: общее количество={}", allOrders.size());
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
