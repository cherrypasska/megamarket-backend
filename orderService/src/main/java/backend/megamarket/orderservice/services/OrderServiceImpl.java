package backend.megamarket.orderservice.services;

import backend.megamarket.orderservice.controller.InsufficientQuantityException;
import backend.megamarket.orderservice.controller.NotificationException;
import backend.megamarket.orderservice.dto.OrderItemDto;
import backend.megamarket.orderservice.mapper.InventoryRequestMapper;
import backend.megamarket.orderservice.mapper.OrderToDtoMapper;
import backend.megamarket.orderservice.mapper.ProductQueryMapper;
import backend.megamarket.orderservice.repository.UserRepository;
import client.inventory.response.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.MDC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import backend.megamarket.orderservice.controller.EmptyOrderException;

import java.util.List;
import java.util.stream.Collectors;

import static client.inventory.response.grpc.ProductStatus.INSUFFICIENT_QUANTITY;

/**
 * Реализация сервиса для работы с заказами.
 * <p>
 * Выполняет проверку наличия товаров на складе,
 * отправляет подтвержденные заказы в Kafka,
 * обрабатывает исключения при недостаточном количестве товара и пустом заказе.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;

    private final KafkaMessagingService kafkaMessagingService;

    private final OrderToDtoMapper orderToDtoMapper;

    private final ProductQueryMapper productQueryMapper;

    private final InventoryRequestMapper inventoryRequestMapper;

    @Value("${grpc.inventory.port}")
    private int inventoryPort;

    /**
     * Проверяет наличие товаров в заказе на складе.
     * Если заказ пустой — выбрасывает {@link EmptyOrderException}.
     * Если какой-либо товар отсутствует или недостаточно — выбрасывает {@link InsufficientQuantityException}.
     * После успешной проверки отправляет заказ в очередь Kafka.
     *
     * @param items список элементов заказа {@link OrderItemDto}
     * @throws EmptyOrderException           если заказ пустой
     * @throws InsufficientQuantityException если количество товаров на складе недостаточно
     */
    @Override
    public void checkOrder(List<OrderItemDto> items) {
        Long orderId = System.currentTimeMillis();
        log.info("[ID:" + orderId +"] формирование заказа" + '\n' + items);
        MDC.put("orderId", String.valueOf(orderId));

        if (items.isEmpty()) {
            log.warn("[ID:" + orderId +"] Попытка оформить пустой заказ");
            throw new EmptyOrderException("Заказ не может быть пустым!");
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", inventoryPort)
                .usePlaintext()
                .build();

        try {
            var stub = InventoryServiceGrpc.newBlockingStub(channel);

            List<ProductQueryDto> productQueries = productQueryMapper.map(items);
            InventoryRequestDto request = inventoryRequestMapper.map(productQueries, orderId);

            log.info("[ID:" + orderId +"] Отправка заказа в Inventory Service");

            var response = stub.checkInventory(request);

            log.info("[ID:" + orderId +"] Наличие проверено в Inventory Service и прибыло в OrderService");

            Long userId = userRepository.findByUsername(
                    SecurityContextHolder.getContext().getAuthentication().getName()
            ).orElseThrow().getId();
            MDC.put("userId", String.valueOf(userId));

            var unavailableItems = response.getItemsList().stream()
                    .filter(item -> item.getStatus() == INSUFFICIENT_QUANTITY)
                    .toList();

            if (!unavailableItems.isEmpty()) {
                String errorMessage = unavailableItems.stream()
                        .map(item -> "Товар ID: " + item.getProductId() + " - недостаточно на складе")
                        .collect(Collectors.joining("; "));
                log.error("[ID" + orderId +"] Недостаточно товаров на складе");
                throw new InsufficientQuantityException(errorMessage);
            }

            try {
                kafkaMessagingService.sendOrder(
                        orderToDtoMapper.convertToDTO(response, userId, items)
                );
                log.info("[ID: {} ] Заказ отправлен в Kafka: userId={}",  orderId, userId);
            } catch (Exception e) {
                throw new NotificationException("[ID:" + orderId + "] Ошибка при отправке заказа в Kafka", e);
            }

        } catch (InsufficientQuantityException | NotificationException e) {
            throw e;
        } catch (Exception e) {
            log.error("[ID:" + orderId + "] Неизвестная ошибка: {}", e.getMessage(), e);
            throw new RuntimeException("[ID:" + orderId + "] Внутренняя ошибка сервиса", e);
        } finally {
            channel.shutdown();
            log.debug("gRPC-канал закрыт");
        }
    }
}