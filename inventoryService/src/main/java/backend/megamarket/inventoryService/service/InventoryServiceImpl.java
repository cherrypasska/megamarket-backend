package backend.megamarket.inventoryservice.service;

import backend.megamarket.inventoryservice.repository.ProductRepository;
import backend.megamarket.inventoryservice.entity.ProductEntity;
import client.inventory_service.response.grpc.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Реализация gRPC-сервиса управления запасами продуктов.
 * Предоставляет методы проверки наличия товаров и обновления количества при заказе.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase implements InventoryService {
    private final ProductRepository productRepository;
    private Server server;

    /**
     * Инициализирует и запускает gRPC-сервер на порту 9090.
     *
     * @throws IOException если сервер не может быть запущен
     */
    @PostConstruct
    @Override
    public void start() throws IOException {
        server = ServerBuilder
                .forPort(9090)
                .addService(new InventoryServiceImpl(productRepository))
                .build()
                .start();
        log.info("Server started, listening on " + server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server != null) {
                server.shutdown();
            }
        }));
    }

    /**
     * Проверяет наличие товаров на складе по заданному заказу.
     * Отвечает списком продуктов с их статусами.
     *
     * @param request          запрос с идентификатором заказа и списком товаров
     * @param responseObserver объект для передачи ответа
     */
    @Override
    public void checkInventory(client.inventory_service.response.grpc.InventoryRequestDto request,
                               StreamObserver<client.inventory_service.response.grpc.InventoryResponseDto> responseObserver) {

        Long orderId = request.getOrderId();

        List<client.inventory_service.response.grpc.ProductQueryDto> order = request.getProductsList();

        List<CompletableFuture<ProductInfoDto>> futures = order.stream()
                .map(query -> CompletableFuture.supplyAsync(() -> {
                    Long productId = query.getProductId();
                    int requestedQuantity = (int) query.getQuantity();

                    Optional<ProductEntity> optionalProduct = productRepository.findById(productId);

                    if (optionalProduct.isEmpty()) {
                        return client.inventory_service.response.grpc.ProductInfoDto.newBuilder()
                                .setProductId(productId)
                                .setName("")
                                .setPrice(0.0)
                                .setDiscount(0.0)
                                .setAvailableQuantity(0)
                                .setStatus(client.inventory_service.response.grpc.ProductStatus.INSUFFICIENT_QUANTITY)
                                .build();
                    }

                    ProductEntity product = optionalProduct.get();

                    client.inventory_service.response.grpc.ProductStatus status =
                            product.getQuantity() >= requestedQuantity
                                    ? client.inventory_service.response.grpc.ProductStatus.OK
                                    : client.inventory_service.response.grpc.ProductStatus.INSUFFICIENT_QUANTITY;

                    return client.inventory_service.response.grpc.ProductInfoDto.newBuilder()
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
                    client.inventory_service.response.grpc.InventoryResponseDto response =
                            client.inventory_service.response.grpc.InventoryResponseDto.newBuilder()
                                    .setOrderId(orderId)
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

    /**
     * Обновляет количество товаров на складе на основе подтвержденного заказа.
     * Возвращает информацию о продуктах с их новым количеством и статусом.
     *
     * @param request          запрос с информацией о заказанных товарах
     * @param responseObserver объект для передачи ответа
     */
    @Override
    public void addOrder(client.inventory_service.response.grpc.InventoryRequestDto request,
                         StreamObserver<client.inventory_service.response.grpc.InventoryResponseDto> responseObserver) {
        List<ProductQueryDto> productQueries = request.getProductsList();

        List<ProductInfoDto> updatedProducts = productQueries.stream().map(query -> {
            Long productId = query.getProductId();
            Long orderedQuantity = query.getQuantity();

            Optional<ProductEntity> optionalProduct = productRepository.findById(productId);

            ProductEntity product = optionalProduct.get();
            long currentStock = product.getQuantity();

            if (currentStock < orderedQuantity) {
                return ProductInfoDto.newBuilder()
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

            return ProductInfoDto.newBuilder()
                    .setProductId(productId)
                    .setName(product.getName())
                    .setPrice(product.getPrice())
                    .setDiscount(product.getSale() == null ? 0.0 : product.getSale())
                    .setAvailableQuantity(product.getQuantity())
                    .setStatus(ProductStatus.OK)
                    .build();
        }).toList();
        Long orderId = request.getOrderId();
        InventoryResponseDto response = InventoryResponseDto.newBuilder()
                .setOrderId(orderId)
                .addAllItems(updatedProducts)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}