package backend.megamarket.service.orderService.servise;

import backend.megamarket.service.orderService.dbs.dao.OrderSendEvent;
import client.inventory.response.grpc.InventoryRequest;
import client.inventory.response.grpc.InventoryServiceGrpc;
import client.inventory.response.grpc.ProductQuery;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaOrderConfirmationListener {

    private final OrderService orderService;

    @KafkaListener(topics = "${topic.add-order}", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderConfirmed(String message) {
        try {
            log.info("Получено сообщение: {}", message);
            ObjectMapper mapper = new ObjectMapper();

            List<OrderSendEvent> confirmedOrders = mapper.readValue(
                    message,
                    new TypeReference<List<OrderSendEvent>>() {}
            );

            log.info("Подтвержден заказ: {}", confirmedOrders);

            List<ProductQuery> queries = confirmedOrders.stream()
                    .map(item -> ProductQuery.newBuilder()
                            .setProductId(item.getProductId())
                            .setQuantity(item.getQuantity())
                            .build())
                    .toList();

            InventoryRequest request = InventoryRequest.newBuilder()
                    .addAllProducts(queries)
                    .build();

            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                    .usePlaintext()
                    .build();

            InventoryServiceGrpc.InventoryServiceBlockingStub stub = InventoryServiceGrpc.newBlockingStub(channel);
            var response = stub.addOrder(request);
            log.info("Остаток товаров обновлен через GRPC: {}", response);

            channel.shutdown();
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения: ", e);
        }
    }

}
