package backend.megamarket.orderservice.service;

import backend.megamarket.orderservice.controller.InsufficientQuantityException;
import backend.megamarket.orderservice.dto.OrderItemDto;
import backend.megamarket.orderservice.mapper.InventoryRequestMapper;
import backend.megamarket.orderservice.mapper.OrderToDtoMapper;
import backend.megamarket.orderservice.mapper.ProductQueryMapper;
import backend.megamarket.orderservice.repository.UserRepository;
import client.inventory.response.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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

    Long orderId = System.currentTimeMillis();

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
        if (items.isEmpty()) {
            log.warn("Попытка оформить пустой заказ");
            throw new EmptyOrderException("Заказ не может быть пустым!");
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", inventoryPort)
                .usePlaintext()
                .build();
        try {
            InventoryServiceGrpc.InventoryServiceBlockingStub stub = InventoryServiceGrpc.newBlockingStub(channel);

            List<ProductQueryDto> productQueries = productQueryMapper.map(items);

            InventoryRequestDto request = inventoryRequestMapper.map(productQueries, orderId);

            log.info("Отправка запроса на проверку склада по заказу orderId={}, товаров={}", orderId, items.size());

            var response = stub.checkInventory(request);

            log.info("Получен ответ от InventoryService: {}", response);

            List<ProductInfoDto> order = response.getItemsList();
            Long userId = userRepository.findByUsername(
                    SecurityContextHolder.getContext().getAuthentication().getName()
            ).orElseThrow().getId();

            List<ProductInfoDto> unavailableItems = order.stream()
                    .filter(item -> item.getStatus() == INSUFFICIENT_QUANTITY)
                    .collect(Collectors.toList());

            if (!unavailableItems.isEmpty()) {
                log.warn("Недостаток товаров для заказа orderId={} от userId={}", orderId, userId);
                unavailableItems.forEach(item ->
                        log.warn("📦 Недостаточно товара: productId={}, запрошено={}, статус={}",
                                item.getProductId(), item.getAvailableQuantity(), item.getStatus())
                );
                String errorMessage = unavailableItems.stream()
                        .map(item -> "Ошибка по заказу " + orderId + "\n" + "От user " + userId + "Товар ID: " + item.getProductId() +
                                " - недостаточно на складе")
                        .collect(Collectors.joining("\n"));

                throw new InsufficientQuantityException(errorMessage);
            }

            kafkaMessagingService.sendOrder(
                    orderToDtoMapper.convertToDTO(response, userId, items)
            );
            log.info("Заказ успешно отправлен в Kafka: orderId={}, userId={}", orderId, userId);
        } catch (Exception e) {
            log.error("Ошибка при проверке и отправке заказа orderId={}: {}", orderId, e.getMessage(), e);
            throw e;
        }finally {
            channel.shutdown();
            log.debug("gRPC-канал закрыт: orderId={}", orderId);
        }
    }
}