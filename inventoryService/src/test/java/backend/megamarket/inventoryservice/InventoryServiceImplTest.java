package backend.megamarket.inventoryservice;

import backend.megamarket.inventoryservice.entity.ProductEntity;
import backend.megamarket.inventoryservice.repository.ProductRepository;
import backend.megamarket.inventoryservice.service.InventoryServiceImpl;
import client.inventory_service.response.grpc.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    private ProductRepository productRepository;
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        //inventoryService = new InventoryServiceImpl(productRepository);
    }

    @Test
    void testCheckInventory_productAvailable() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setSale(0.1D);
        product.setQuantity(50L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductQueryDto productQuery = ProductQueryDto.newBuilder()
                .setProductId(1L)
                .setQuantity(20)
                .build();

        InventoryRequestDto request = InventoryRequestDto.newBuilder()
                .setOrderId(123L)
                .addProducts(productQuery)
                .build();

        StreamObserver<InventoryResponseDto> responseObserver = mock(StreamObserver.class);

        inventoryService.checkInventory(request, responseObserver);

        ArgumentCaptor<InventoryResponseDto> captor = ArgumentCaptor.forClass(InventoryResponseDto.class);
        verify(responseObserver, timeout(1000)).onNext(captor.capture());

        InventoryResponseDto response = captor.getValue();
        assertEquals(123L, response.getOrderId());
        assertEquals(1, response.getItemsCount());

        ProductInfoDto item = response.getItems(0);
        assertEquals(ProductStatus.OK, item.getStatus());
        assertEquals(50, item.getAvailableQuantity());
    }

    @Test
    void testCheckInventory_productNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        ProductQueryDto productQuery = ProductQueryDto.newBuilder()
                .setProductId(2L)
                .setQuantity(10)
                .build();

        InventoryRequestDto request = InventoryRequestDto.newBuilder()
                .setOrderId(456L)
                .addProducts(productQuery)
                .build();

        StreamObserver<InventoryResponseDto> responseObserver = mock(StreamObserver.class);

        inventoryService.checkInventory(request, responseObserver);

        ArgumentCaptor<InventoryResponseDto> captor = ArgumentCaptor.forClass(InventoryResponseDto.class);
        verify(responseObserver, timeout(1000)).onNext(captor.capture());

        ProductInfoDto item = captor.getValue().getItems(0);
        assertEquals(ProductStatus.INSUFFICIENT_QUANTITY, item.getStatus());
        assertEquals(0, item.getAvailableQuantity());
    }

    @Test
    void testAddOrder_successfulUpdate() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setSale(0.05D);
        product.setQuantity(30L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductQueryDto query = ProductQueryDto.newBuilder()
                .setProductId(1L)
                .setQuantity(10)
                .build();

        InventoryRequestDto request = InventoryRequestDto.newBuilder()
                .setOrderId(777L)
                .addProducts(query)
                .build();

        StreamObserver<InventoryResponseDto> responseObserver = mock(StreamObserver.class);

        inventoryService.addOrder(request, responseObserver);

        ArgumentCaptor<InventoryResponseDto> captor = ArgumentCaptor.forClass(InventoryResponseDto.class);
        verify(responseObserver).onNext(captor.capture());

        ProductInfoDto item = captor.getValue().getItems(0);
        assertEquals(ProductStatus.OK, item.getStatus());
        assertEquals(20, item.getAvailableQuantity());
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void testAddOrder_insufficientStock() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Product X");
        product.setPrice(50.0);
        product.setSale(0.0D);
        product.setQuantity(5L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductQueryDto query = ProductQueryDto.newBuilder()
                .setProductId(1L)
                .setQuantity(10)
                .build();

        InventoryRequestDto request = InventoryRequestDto.newBuilder()
                .setOrderId(888L)
                .addProducts(query)
                .build();

        StreamObserver<InventoryResponseDto> responseObserver = mock(StreamObserver.class);

        inventoryService.addOrder(request, responseObserver);

        ArgumentCaptor<InventoryResponseDto> captor = ArgumentCaptor.forClass(InventoryResponseDto.class);
        verify(responseObserver).onNext(captor.capture());

        ProductInfoDto item = captor.getValue().getItems(0);
        assertEquals(ProductStatus.INSUFFICIENT_QUANTITY, item.getStatus());
        assertEquals(5, item.getAvailableQuantity());
        verify(productRepository, never()).save(any());
    }
}

