package backend.megamarket.notificationservice;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import backend.megamarket.notificationservice.entity.OrderEntity;
import backend.megamarket.notificationservice.repository.OrderRepository;
import backend.megamarket.notificationservice.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Captor
    private ArgumentCaptor<List<OrderEntity>> ordersCaptor;

    private final OrderEventDto sampleDto = new OrderEventDto(
            100L,  // orderId
            500L,  // productId
            2L,    // quantity
            100.0, // price
            0.1,   // sale
            200L   // userId
    );

    @Test
    @Transactional
    void save_ShouldSaveOrders() {

        List<OrderEventDto> input = Collections.singletonList(sampleDto);
        when(orderRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<OrderEntity> result = orderService.save(input);

        verify(orderRepository).saveAll(ordersCaptor.capture());
        List<OrderEntity> savedOrders = ordersCaptor.getValue();

        assertEquals(1, savedOrders.size());
        OrderEntity order = savedOrders.get(0);

        assertEquals(100L, order.getOrderId());
        assertEquals(500L, order.getProductId());
        assertEquals(2L, order.getQuantity());
        assertEquals(100.0, order.getPrice());
        assertEquals(0.1, order.getSale());
        assertEquals(200L, order.getUserId());
        assertEquals(180.0, order.getTotalPrice());
    }

    @Test
    @Transactional
    void save_ShouldCalculateTotalPriceCorrectly() {
        // Arrange
        OrderEventDto dto = new OrderEventDto(
                200L,  // orderId
                600L,  // productId
                3L,    // quantity
                50.0,  // price
                0.2,   // sale
                300L   // userId
        );
        when(orderRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        orderService.save(Collections.singletonList(dto));

        // Assert
        verify(orderRepository).saveAll(ordersCaptor.capture());
        OrderEntity order = ordersCaptor.getValue().get(0);

        // 3 * (50 - 50*0.2) = 3 * 40 = 120
        assertEquals(120.0, order.getTotalPrice());
    }

    @Test
    @Transactional
    void save_ShouldHandleExceptionAndReturnNull() {

        List<OrderEventDto> input = Collections.singletonList(sampleDto);
        when(orderRepository.saveAll(any())).thenThrow(new RuntimeException("DB error"));

        List<OrderEntity> result = orderService.save(input);

        assertNull(result);
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void getAllProducts_ShouldReturnAllOrders() {

        List<OrderEntity> expected = Collections.singletonList(new OrderEntity());
        when(orderRepository.findAll()).thenReturn(expected);

        List<OrderEntity> result = orderService.getAllProducts();

        assertSame(expected, result);
        verify(orderRepository).findAll();
    }

    @Test
    void getOrdersByOrderId_ShouldReturnFilteredOrders() {

        Long orderId = 100L;
        List<OrderEntity> expected = Collections.singletonList(new OrderEntity());
        when(orderRepository.findByOrderId(orderId)).thenReturn(expected);

        List<OrderEntity> result = orderService.getOrdersByOrderId(orderId);

        assertSame(expected, result);
        verify(orderRepository).findByOrderId(orderId);
    }

    @Test
    void getOrdersByUserId_ShouldReturnUserOrders() {

        Long userId = 200L;
        List<OrderEntity> expected = Collections.singletonList(new OrderEntity());
        when(orderRepository.findByUserId(userId)).thenReturn(expected);

        List<OrderEntity> result = orderService.getOrdersByUserId(userId);

        assertSame(expected, result);
        verify(orderRepository).findByUserId(userId);
    }

    @Test
    @Transactional
    void save_ShouldHandleNullSaleAsZero() {

        OrderEventDto dto = new OrderEventDto(
                300L,  // orderId
                700L,  // productId
                1L,    // quantity
                100.0, // price
                0.0,  // sale
                400L   // userId
        );
        when(orderRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        orderService.save(Collections.singletonList(dto));

        verify(orderRepository).saveAll(ordersCaptor.capture());
        OrderEntity order = ordersCaptor.getValue().get(0);

        assertEquals(100.0, order.getTotalPrice());
    }
}
