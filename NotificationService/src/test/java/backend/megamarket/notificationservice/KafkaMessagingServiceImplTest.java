package backend.megamarket.notificationservice;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import backend.megamarket.notificationservice.dto.OrderItemDto;
import backend.megamarket.notificationservice.service.KafkaMessagingServiceImpl;
import backend.megamarket.notificationservice.service.KafkaProducerService;
import backend.megamarket.notificationservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class KafkaMessagingServiceImplTest {

    @Mock
    private OrderService orderService;

    @Mock
    private KafkaProducerService kafkaProducer;

    @InjectMocks
    private KafkaMessagingServiceImpl kafkaMessagingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_ShouldCallServicesAndReturnSameDto() {
        // Arrange
        OrderItemDto item = new OrderItemDto();
        item.setProductId(1L);
        item.setQuantity(2L);

        OrderEventDto inputEvent = new OrderEventDto();
        inputEvent.setOrderId(100L);
        inputEvent.setUserId(200L);
        inputEvent.setProducts(List.of(item));

        // Act
        OrderEventDto result = kafkaMessagingService.createOrder(inputEvent);

        // Assert
        verify(orderService, times(1)).save(inputEvent);
        verify(kafkaProducer, times(1)).sendOrderConfirmation(inputEvent);
        assertEquals(inputEvent, result);
    }
}
