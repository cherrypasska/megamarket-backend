package backend.megamarket.notificationservice;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import backend.megamarket.notificationservice.dto.OrderItemDto;
import backend.megamarket.notificationservice.entity.OrderEntity;
import backend.megamarket.notificationservice.entity.OrderProductsEntity;
import backend.megamarket.notificationservice.mapper.OrderEntityMapper;
import backend.megamarket.notificationservice.mapper.OrderProductsEntityMapper;
import backend.megamarket.notificationservice.repository.OrderProductsRepository;
import backend.megamarket.notificationservice.repository.OrderRepository;
import backend.megamarket.notificationservice.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductsRepository orderProductsRepository;

    @Mock
    private OrderEntityMapper orderEntityMapper;

    @Mock
    private OrderProductsEntityMapper orderProductsEntityMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldReturnSavedEntities_WhenSuccess() {
        // Arrange
        OrderItemDto productDto = new OrderItemDto();
        productDto.setProductId(1L);
        productDto.setQuantity(2L);
        productDto.setPrice(100.0);
        productDto.setSale(0.1);

        OrderEventDto dto = new OrderEventDto();
        dto.setOrderId(123L);
        dto.setUserId(456L);
        dto.setProducts(List.of(productDto));

        OrderEntity orderEntity = OrderEntity.builder()
                .orderId(dto.getOrderId())
                .userId(dto.getUserId())
                .totalPrice(0.0)
                .build();

        OrderProductsEntity productEntity = new OrderProductsEntity();
        productEntity.setProductId(1L);

        when(orderEntityMapper.orderEntityMapping(dto)).thenReturn(orderEntity);
        when(orderProductsEntityMapper.OrderProductsEntityMapping(dto, productDto)).thenReturn(productEntity);

        // Act
        List<OrderProductsEntity> result = orderService.save(dto);

        // Assert
        verify(orderRepository).save(orderEntity);
        verify(orderProductsRepository).saveAll(List.of(productEntity));
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getProductId());
    }

    @Test
    void save_ShouldReturnNull_WhenExceptionOccurs() {
        // Arrange
        OrderEventDto dto = new OrderEventDto();
        dto.setOrderId(1L);
        dto.setUserId(2L);
        dto.setProducts(List.of(new OrderItemDto()));

        when(orderEntityMapper.orderEntityMapping(any())).thenThrow(new RuntimeException("Ошибка"));

        // Act
        List<OrderProductsEntity> result = orderService.save(dto);

        // Assert
        assertNull(result);
    }

    @Test
    void getAllProducts_ShouldReturnAllOrders() {
        // Arrange
        OrderEntity order1 = OrderEntity.builder().orderId(1L).build();
        OrderEntity order2 = OrderEntity.builder().orderId(2L).build();
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        // Act
        List<OrderEntity> result = orderService.getAllProducts();

        // Assert
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrdersByOrderId_ShouldReturnMatchingOrders() {
        // Arrange
        Long orderId = 5L;
        OrderEntity order = OrderEntity.builder().orderId(orderId).build();
        when(orderRepository.findByOrderId(orderId)).thenReturn(List.of(order));

        // Act
        List<OrderEntity> result = orderService.getOrdersByOrderId(orderId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(orderId, result.get(0).getOrderId());
    }

    @Test
    void getOrdersByUserId_ShouldReturnUserOrders() {
        // Arrange
        Long userId = 999L;
        OrderEntity order = OrderEntity.builder().userId(userId).build();
        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));

        // Act
        List<OrderEntity> result = orderService.getOrdersByUserId(userId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
    }
}
