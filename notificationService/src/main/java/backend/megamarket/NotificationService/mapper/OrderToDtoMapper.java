package backend.megamarket.notificationservice.mapper;

import backend.megamarket.notificationservice.dto.OrderProductDto;
import backend.megamarket.notificationservice.dto.OrdersDto;
import backend.megamarket.notificationservice.entity.OrderEntity;
import backend.megamarket.notificationservice.entity.OrderProductsEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Компонент для преобразования сущностей заказов и продуктов заказов
 * в соответствующие DTO объекты для передачи данных.
 */
@Component
public class OrderToDtoMapper {

    /**
     * Преобразует {@link OrderEntity} и список {@link OrderProductsEntity} в DTO {@link OrdersDto}.
     *
     * @param order    сущность заказа {@link OrderEntity}
     * @param products список продуктов заказа {@link OrderProductsEntity}
     * @return DTO объекта {@link OrdersDto}, содержащий информацию о заказе и его продуктах
     */
    public OrdersDto toDto(OrderEntity order, List<OrderProductsEntity> products) {
        OrdersDto dto = new OrdersDto();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setTotalPrice(order.getTotalPrice());

        List<OrderProductDto> productDtos = products.stream().map(p -> {
            OrderProductDto pd = new OrderProductDto();
            pd.setProductId(p.getProductId());
            pd.setQuantity(p.getQuantity());
            pd.setPrice(p.getPrice());
            pd.setSale(p.getSale());
            return pd;
        }).toList();

        dto.setProducts(productDtos);
        return dto;
    }
}
