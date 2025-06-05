package backend.megamarket.service.orderService.servise;

import backend.megamarket.service.orderService.dbs.dao.*;
import client.inventory.response.grpc.InventoryRequest;
import client.inventory.response.grpc.InventoryServiceGrpc;
import client.inventory.response.grpc.ProductQuery;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService{
    public void сheckOrder(List<OrderItem> items) {
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

        stub.checkInventory(request);
    }
}