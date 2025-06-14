package backend.megamarket.service;

import backend.megamarket.service.orderservice.controller.EmptyOrderException;
import backend.megamarket.service.orderservice.dto.OrderItemDto;
import backend.megamarket.service.orderservice.entity.UserEntity;
import backend.megamarket.service.orderservice.repository.UserRepository;
import backend.megamarket.service.orderservice.service.OrderServiceImpl;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Spy
    @SuppressWarnings("unused")
    private ManagedChannel dummyChannel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockSecurityContext("testuser", 1L);
    }

    private void mockSecurityContext(String username, Long userId) {
        SecurityContext context = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    }

    @Test
    void checkOrder_shouldThrowEmptyOrderException() {

        List<OrderItemDto> emptyItems = List.of();
        EmptyOrderException exception = assertThrows(EmptyOrderException.class, () ->
                orderService.checkOrder(emptyItems)
        );
        assertEquals("Заказ не может быть пустым!", exception.getMessage());
    }
}
