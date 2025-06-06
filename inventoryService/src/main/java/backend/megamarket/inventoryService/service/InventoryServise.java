package backend.megamarket.inventoryService.service;

import backend.megamarket.inventoryService.service.dto.dao.ProductRepository;
import backend.megamarket.inventoryService.service.dto.model.Product;
import client.inventory_service.response.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class InventoryServise extends InventoryServiceGrpc.InventoryServiceImplBase {
    private final ProductRepository productRepository;
    @Override
    public void checkInventory(client.inventory_service.response.grpc.InventoryRequest request,
                               StreamObserver<client.inventory_service.response.grpc.InventoryResponse> responseObserver) {

        List<client.inventory_service.response.grpc.ProductQuery> order = request.getProductsList();

        List<CompletableFuture<ProductInfo>> futures = order.stream()
                .map(query -> CompletableFuture.supplyAsync(() -> {
                    Long productId = query.getProductId();
                    int requestedQuantity = (int) query.getQuantity();

                    Optional<Product> optionalProduct = productRepository.findById(productId);

                    if (optionalProduct.isEmpty()) {
                        return client.inventory_service.response.grpc.ProductInfo.newBuilder()
                                .setProductId(productId)
                                .setName("")
                                .setPrice(0.0)
                                .setDiscount(0.0)
                                .setAvailableQuantity(0)
                                .setStatus(client.inventory_service.response.grpc.ProductStatus.NOT_FOUND)
                                .build();
                    }

                    Product product = optionalProduct.get();

                    client.inventory_service.response.grpc.ProductStatus status =
                            product.getQuantity() >= requestedQuantity
                                    ? client.inventory_service.response.grpc.ProductStatus.OK
                                    : client.inventory_service.response.grpc.ProductStatus.INSUFFICIENT_QUANTITY;

                    return client.inventory_service.response.grpc.ProductInfo.newBuilder()
                            .setProductId(product.getId())
                            .setName(product.getName())
                            .setPrice(product.getPrice())
                            .setDiscount(product.getSale() == null ? 0.0 : product.getSale())
                            .setAvailableQuantity(product.getQuantity())
                            .setStatus(status)
                            .build();
                })).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .thenAccept(productInfos -> {
                    client.inventory_service.response.grpc.InventoryResponse response =
                            client.inventory_service.response.grpc.InventoryResponse.newBuilder()
                                    .addAllItems(productInfos)
                                    .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    responseObserver.onError(io.grpc.Status.INTERNAL
                            .withDescription("Inventory check failed: " + ex.getMessage())
                            .asRuntimeException());
                    return null;
                });
    }
    @Override
    public void addOrder(client.inventory_service.response.grpc.InventoryRequest request,
                               StreamObserver<client.inventory_service.response.grpc.InventoryResponse> responseObserver) {
        List<ProductQuery> productQueries = request.getProductsList();

        List<ProductInfo> updatedProducts = productQueries.stream().map(query -> {
            Long productId = query.getProductId();
            Long orderedQuantity = query.getQuantity();

            Optional<Product> optionalProduct = productRepository.findById(productId);

            Product product = optionalProduct.get();
            long currentStock = product.getQuantity();

            if (currentStock < orderedQuantity) {
                return ProductInfo.newBuilder()
                        .setProductId(productId)
                        .setName(product.getName())
                        .setPrice(product.getPrice())
                        .setDiscount(product.getSale() == null ? 0.0 : product.getSale())
                        .setAvailableQuantity(currentStock)
                        .setStatus(ProductStatus.INSUFFICIENT_QUANTITY)
                        .build();
            }
            product.setQuantity(currentStock - orderedQuantity);
            productRepository.save(product);

            return ProductInfo.newBuilder()
                    .setProductId(productId)
                    .setName(product.getName())
                    .setPrice(product.getPrice())
                    .setDiscount(product.getSale() == null ? 0.0 : product.getSale())
                    .setAvailableQuantity(product.getQuantity())
                    .setStatus(ProductStatus.OK)
                    .build();
        }).toList();

        InventoryResponse response = InventoryResponse.newBuilder()
                .addAllItems(updatedProducts)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}