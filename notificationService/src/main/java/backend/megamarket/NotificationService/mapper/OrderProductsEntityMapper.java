package backend.megamarket.notificationservice.mapper;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import backend.megamarket.notificationservice.dto.OrderItemDto;
import backend.megamarket.notificationservice.entity.OrderProductsEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования данных из {@link OrderEventDto} и {@link OrderItemDto}
 * в сущность {@link OrderProductsEntity}.
 * <p>
 * Используется при сохранении информации о продуктах в заказе.
 */
@Component
@RequiredArgsConstructor
public class OrderProductsEntityMapper {

    /**
     * Преобразует данные одного элемента заказа из DTO в сущность {@link OrderProductsEntity}.
     *
     * @param clientDto объект с информацией о заказе
     * @param p         элемент заказа (товар)
     * @return сущность {@link OrderProductsEntity} для сохранения в базе данных
     */
    public OrderProductsEntity OrderProductsEntityMapping(OrderEventDto clientDto, OrderItemDto p){
        OrderProductsEntity newOrder = new OrderProductsEntity();
        newOrder.setOrderId(clientDto.getOrderId());
        newOrder.setPrice(p.getPrice());
        newOrder.setQuantity(p.getQuantity());
        newOrder.setSale(p.getSale());
        newOrder.setProductId(p.getProductId());
        return newOrder;
    }
}
