package backend.megamarket.orderservice.service;

import backend.megamarket.orderservice.dto.OrderEventDto;
import backend.megamarket.orderservice.dto.OrderSendEventDto;
import client.inventory.response.grpc.InventoryRequestDto;
import client.inventory.response.grpc.InventoryServiceGrpc;
import client.inventory.response.grpc.ProductQueryDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация слушателя подтверждений заказов из Kafka.
 * <p>
 * При получении сообщения из Kafka-топика с подтверждением заказа,
 * обновляет остатки товаров через gRPC-вызов к InventoryService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaOrderConfirmationListenerImpl {

    @Value("${grpc.inventory.port}")
    private int inventoryPort;

    /**
     * Обработка сообщения о подтверждении заказа из Kafka.
     * <p>
     * Метод выполняет следующие действия:
     * <ul>
     *     <li>Десериализует JSON-сообщение в список объектов {@link OrderSendEventDto}</li>
     *     <li>Формирует gRPC запрос к InventoryService для обновления остатков товаров</li>
     *     <li>Отправляет запрос и логирует ответ сервиса</li>
     *     <li>Обрабатывает исключения и логирует ошибки</li>
     * </ul>
     *
     * @param message сообщение с подтверждением заказа в формате JSON
     */

    @KafkaListener(topics = "${topic.add-order}", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderConfirmed(String message) {
        try {
            log.info("Получено сообщение из Kafka: {}", message);
            ObjectMapper mapper = new ObjectMapper();

            OrderEventDto confirmedOrders = mapper.readValue(
                    message,
                    OrderEventDto.class
            );

            log.info("Подтвержден заказ: orderId={}, userId={}, товаров={}",
                    confirmedOrders.getOrderId(), confirmedOrders.getUserId(), confirmedOrders.getProducts().size());;

            List<ProductQueryDto> queries = confirmedOrders.getProducts().stream()
                    .map(item -> ProductQueryDto.newBuilder()
                            .setProductId(item.getProductId())
                            .setQuantity(item.getQuantity())
                            .build())
                    .toList();

            InventoryRequestDto request = InventoryRequestDto.newBuilder()
                    .addAllProducts(queries)
                    .build();

            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", inventoryPort)
                    .usePlaintext()
                    .build();

            InventoryServiceGrpc.InventoryServiceBlockingStub stub = InventoryServiceGrpc.newBlockingStub(channel);
            var response = stub.addOrder(request);
            log.info("Остатки обновлены в InventoryService через gRPC. Ответ: {}", response);

            channel.shutdown();
        } catch (Exception e) {
            log.error("Ошибка при обработке Kafka-сообщения о заказе: {}", e.getMessage(), e);
        }
    }

}
