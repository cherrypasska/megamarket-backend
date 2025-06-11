package backend.megamarket.notificationservice;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import backend.megamarket.notificationservice.service.KafkaMessagingServiceImpl;
import backend.megamarket.notificationservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaMessagingServiceImplTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private KafkaMessagingServiceImpl kafkaService;

    @Test
    void createOrder_ShouldProcessEventsCorrectly() {

        List<OrderEventDto> inputEvents = Collections.singletonList(new OrderEventDto());
        List<OrderEventDto> mappedOrders = Collections.singletonList(new OrderEventDto());

        when(modelMapper.map(inputEvents, List.class)).thenReturn(mappedOrders);

        List<OrderEventDto> result = kafkaService.createOrder(inputEvents);

        verify(orderService, times(1)).save(mappedOrders);
        verifyNoMoreInteractions(orderService);
        assertThat(result).isEqualTo(inputEvents);
    }

    @Test
    void createOrder_ShouldLogEventAndHandleEmptyList() {

        List<OrderEventDto> emptyEvents = Collections.emptyList();
        when(modelMapper.map(emptyEvents, List.class)).thenReturn(Collections.emptyList());

        kafkaService.createOrder(emptyEvents);

        verify(orderService).save(Collections.emptyList());
    }

    @Test
    void createOrder_ShouldHandleMappingException() {

        List<OrderEventDto> events = Collections.singletonList(new OrderEventDto());
        when(modelMapper.map(events, List.class)).thenThrow(new RuntimeException("Mapping failed"));

        assertThrows(RuntimeException.class, () -> kafkaService.createOrder(events));
        verifyNoInteractions(orderService);
    }
}