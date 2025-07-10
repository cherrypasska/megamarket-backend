package backend.megamarket.inventoryservice.service;

import backend.megamarket.inventoryservice.mapper.InventoryResponseMapper;
import backend.megamarket.inventoryservice.mapper.ProductInfoMapper;
import backend.megamarket.inventoryservice.repository.ProductRepository;
import backend.megamarket.inventoryservice.entity.ProductEntity;
import client.inventory_service.response.grpc.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final ProductInfoMapper productInfoMapper;

    private final InventoryResponseMapper inventoryResponseMapper;

    private Server server;

    @Value("${grpc.server.port}")
    private int grpcPort;

    /**
     * Инициализирует и запускает gRPC-сервер на порту 9090.
     *
     * @throws IOException если сервер не может быть запущен
     */
    @PostConstruct
    @Override
    public void start() throws IOException {
        server = ServerBuilder
                .forPort(grpcPort)
                .addService(this)
                .build()
                .start();

        log.info("gRPC server started on port {}", grpcPort);

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

        log.info("[ID:" + orderId + "] Заказ прибыл в Inventory Service");

        log.info("[ID:" + orderId + "] Началась проверка наличия на склад");

        List<client.inventory_service.response.grpc.ProductQueryDto> order = request.getProductsList();

        List<CompletableFuture<ProductInfoDto>> futures = order.stream()
                .map(query -> CompletableFuture.supplyAsync(() -> {
                    Long productId = query.getProductId();
                    int requestedQuantity = (int) query.getQuantity();

                    Optional<ProductEntity> optionalProduct = productRepository.findById(productId);

                    if (optionalProduct.isEmpty()) {
                        log.warn("[ID:{}] Продукт с ID {} не найден на складе", orderId, productId);

                        return productInfoMapper.emptyMapping(productId);
                    }

                    ProductEntity product = optionalProduct.get();

                    client.inventory_service.response.grpc.ProductStatus status =
                            product.getQuantity() >= requestedQuantity
                                    ? client.inventory_service.response.grpc.ProductStatus.OK
                                    : client.inventory_service.response.grpc.ProductStatus.INSUFFICIENT_QUANTITY;
                    if (status == client.inventory_service.response.grpc.ProductStatus.OK) {
                        log.debug("[ID:{}] Продукт ID {} достаточно на складе", orderId, productId);
                    } else {
                        log.warn("[ID:{}] Недостаточно продукта ID {}: нужно {}, есть {}",
                                orderId, productId, requestedQuantity, product.getQuantity());
                    }
                    return productInfoMapper.argMapper(product, status);
                })).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .thenAccept(productInfos -> {
                    client.inventory_service.response.grpc.InventoryResponseDto response = productInfoMapper.responseMapper(productInfos, orderId);
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                    log.info("[ID:{}] Проверка завершена: {} товаров обработано", orderId, productInfos.size());
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
        Long orderId = request.getOrderId();
        log.info("[ID:{}]Начато обновление запасов по подтвержденному заказу", orderId);
        List<ProductQueryDto> productQueries = request.getProductsList();

        List<ProductInfoDto> updatedProducts = productQueries.stream().map(query -> {

            Long productId = query.getProductId();
            Long orderedQuantity = query.getQuantity();

            Optional<ProductEntity> optionalProduct = productRepository.findById(productId);

            ProductEntity product = optionalProduct.get();
            long currentStock = product.getQuantity();

            if (currentStock < orderedQuantity) {
                log.warn("[ID{}] Недостаточно товара ID {}: нужно {}, есть {}",
                        orderId, productId, orderedQuantity, currentStock);
                return productInfoMapper.productInfoIQMapping(productId, product, currentStock);
            }
            product.setQuantity(currentStock - orderedQuantity);
            productRepository.save(product);
            log.info("ID[{}] Обновлены запасы товара ID {}: новое количество {}",
                    orderId, productId, product.getQuantity());

            return productInfoMapper.productInfoOkMapping(productId, product, currentStock);
        }).toList();
        InventoryResponseDto response = inventoryResponseMapper.InventoryResponseMapping(orderId, updatedProducts);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        log.info("[ID{}] Обработка заказа завершена. Обновлено {} продуктов.", orderId, updatedProducts.size());
    }
}