package backend.megamarket.service.orderservice.service;

import backend.megamarket.service.orderservice.controller.InsufficientQuantityException;
import backend.megamarket.service.orderservice.dto.OrderItemDto;
import backend.megamarket.service.orderservice.mapper.OrderToDtoMapper;
import backend.megamarket.service.orderservice.repository.UserRepository;
import client.inventory.response.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import backend.megamarket.service.orderservice.controller.EmptyOrderException;

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

    private final ModelMapper modelMapper;

    private final OrderToDtoMapper orderToDtoMapper;

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
            throw new EmptyOrderException("Заказ не может быть пустым!");
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        try {
            InventoryServiceGrpc.InventoryServiceBlockingStub stub = InventoryServiceGrpc.newBlockingStub(channel);

            List<ProductQueryDto> productQueries = items.stream()
                    .map(item -> ProductQueryDto.newBuilder()
                            .setProductId(item.getProductId())
                            .setQuantity(item.getQuantity())
                            .build())
                    .toList();

            Long orderId = System.currentTimeMillis();

            InventoryRequestDto request = InventoryRequestDto.newBuilder()
                    .setOrderId(orderId)
                    .addAllProducts(productQueries)
                    .build();

            var response = stub.checkInventory(request);
            log.info("Inventory response: " + response);

            List<ProductInfoDto> order = response.getItemsList();
            Long userId = userRepository.findByUsername(
                    SecurityContextHolder.getContext().getAuthentication().getName()
            ).orElseThrow().getId();

            List<ProductInfoDto> unavailableItems = order.stream()
                    .filter(item -> item.getStatus() == INSUFFICIENT_QUANTITY)
                    .collect(Collectors.toList());

            if (!unavailableItems.isEmpty()) {
                String errorMessage = unavailableItems.stream()
                        .map(item -> "Ошибка по заказу " + orderId + "\n" + "От user " + userId + "Товар ID: " + item.getProductId() +
                                " - недостаточно на складе")
                        .collect(Collectors.joining("\n"));

                throw new InsufficientQuantityException(errorMessage);
            }

            kafkaMessagingService.sendOrder(
                    modelMapper.map(
                            orderToDtoMapper.convertToDTO(response, userId, items),
                            List.class
                    )
            );
            log.info("Send order from producer {}", response);
        } finally {
            channel.shutdown();
        }
    }
}