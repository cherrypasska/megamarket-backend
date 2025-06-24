package backend.megamarket.service.orderservice.mapper;

import backend.megamarket.service.orderservice.dto.OrderItemDto;
import backend.megamarket.service.orderservice.dto.OrderSendEventDto;
import client.inventory.response.grpc.InventoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Маппер для преобразования данных из ответа инвентаря и списка заказанных товаров в список DTO для отправки заказа.
 */
@RequiredArgsConstructor
@Configuration
public class OrderToDtoMapper {

    /**
     * Преобразует данные из {@link InventoryResponseDto} и списка {@link OrderItemDto} в список {@link OrderSendEventDto},
     * сопоставляя количество товаров по идентификатору продукта и добавляя информацию о пользователе и заказе.
     *
     * @param response ответ инвентаря с информацией о продуктах и заказе
     * @param userId   идентификатор пользователя, который создает заказ
     * @param items    список элементов заказа с productId и количеством
     * @return список DTO для отправки заказа {@link OrderSendEventDto}
     */
    public List<OrderSendEventDto> convertToDTO(InventoryResponseDto response, Long userId, List<OrderItemDto> items) {
        Map<Long, Long> quantityByProductId = items.stream()
                .collect(java.util.stream.Collectors.toMap(OrderItemDto::getProductId, OrderItemDto::getQuantity));

        return response.getItemsList().stream()
                .map(p -> {
                    OrderSendEventDto dto = new OrderSendEventDto();
                    dto.setOrderId(response.getOrderId());
                    dto.setProductId(p.getProductId());
                    dto.setQuantity(quantityByProductId.getOrDefault(p.getProductId(), 0L));
                    dto.setPrice(p.getPrice());
                    dto.setSale(p.getDiscount());
                    dto.setUserId(userId);
                    return dto;
                }).toList();
    }
}
