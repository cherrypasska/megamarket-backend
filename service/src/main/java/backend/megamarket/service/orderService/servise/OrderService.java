package backend.megamarket.service.orderService.servise;

import backend.megamarket.service.orderService.dbs.dao.*;
import backend.megamarket.service.orderService.dbs.repository.UserRepository;
import client.inventory.response.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static client.inventory.response.grpc.ProductStatus.INSUFFICIENT_QUANTITY;
import static client.inventory.response.grpc.ProductStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService{
    private final UserService userService;
    private final UserRepository userRepository;
    private final KafkaMessagingService kafkaMessagingService;
    private final ModelMapper modelMapper;

    private List<OrderSendEvent> convertToDTO(InventoryResponse response, Long userId, List<OrderItem> items) {
        Map<Long, Long> quantityByProductId = items.stream()
                .collect(java.util.stream.Collectors.toMap(OrderItem::productId, OrderItem::quantity));

        return response.getItemsList().stream()
                .map(p -> {
                    OrderSendEvent dto = new OrderSendEvent();
                    dto.setProductId(p.getProductId());
                    dto.setQuantity(quantityByProductId.getOrDefault(p.getProductId(), 0L));
                    dto.setPrice(p.getPrice());
                    dto.setSale(p.getDiscount());
                    dto.setUserId(userId);
                    return dto;
                }).toList();
    }

    public void сheckOrder(List<OrderItem> items) {
        if(items.isEmpty()){
            System.out.println("Заказ не может быть пустым!");
            return;
        }
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        InventoryServiceGrpc.InventoryServiceBlockingStub stub = InventoryServiceGrpc.newBlockingStub(channel);
        List<ProductQuery> productQueries = items.stream()
                .map(item -> ProductQuery.newBuilder()
                        .setProductId(item.productId())
                        .setQuantity(item.quantity())
                        .build())
                .toList();
        InventoryRequest request = InventoryRequest.newBuilder()
                .addAllProducts(productQueries)
                .build();
        var response = stub.checkInventory(request);
        System.out.println("Inventory response: " + response);
        List<ProductInfo> order = response.getItemsList();
        boolean allFound = order.stream()
                .noneMatch(item -> item.getStatus() == NOT_FOUND);
        if(!allFound){
            System.out.println("Не все товары есть в наличии");
            return;
        }
        if(allFound != order.stream()
                .noneMatch(item -> item.getStatus() == INSUFFICIENT_QUANTITY)){
            System.out.println("Некоторых товаров не хватает");
            return;
        }
        channel.shutdown();
        Long userId = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get().getId();
        kafkaMessagingService.sendOrder(modelMapper.map(convertToDTO(response, userId, items), List.class));
        log.info("Send order from producer {}", response);
        //реализация проверки что заказ создался (кафка)
        //stub.addOrder(request);
    }
}