package backend.megamarket.notificationservice.mapper;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import backend.megamarket.notificationservice.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;

/**
 * Маппер для преобразования данных из {@link OrderEventDto}
 * в сущность {@link OrderEntity}.
 * <p>
 * Используется при создании новой записи о заказе.
 */
@Mapper
@RequiredArgsConstructor
public class OrderEntityMapper {

    /**
     * Преобразует объект {@link OrderEventDto} в сущность {@link OrderEntity}
     * для последующего сохранения в базу данных.
     *
     * @param clientDto DTO заказа, содержащий идентификаторы и пользователя
     * @return новая сущность {@link OrderEntity}
     */
    public OrderEntity orderEntityMapping(OrderEventDto clientDto) {
        OrderEntity orderEntity = OrderEntity.builder()
                .orderId(clientDto.getOrderId())
                .userId(clientDto.getUserId())
                .totalPrice(0.0)
                .build();
        return orderEntity;
    }
}
