package backend.megamarket.orderservice.mapper;

import backend.megamarket.orderservice.dto.OrderItemDto;
import client.inventory.response.grpc.ProductQueryDto;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Маппер для преобразования DTO элементов заказа {@link OrderItemDto}
 * <p>
 * Преобразованные объекты {@code ProductQueryDto} применяются при проверке остатков
 * или списании товаров в методах {@code checkInventory()} и {@code addOrder()}.
 */
@Component
@RequiredArgsConstructor
public class ProductQueryMapper {

    /**
     * Преобразует список элементов заказа {@link OrderItemDto} в список gRPC-объектов {@link ProductQueryDto}.
     *
     * @param items список элементов заказа
     * @return список gRPC-объектов {@link ProductQueryDto}, готовых для запроса в инвентарный сервис
     */
    public List<ProductQueryDto> map(List<OrderItemDto> items){
        List<ProductQueryDto> productQueries = items.stream()
                .map(item -> ProductQueryDto.newBuilder()
                        .setProductId(item.getProductId())
                        .setQuantity(item.getQuantity())
                        .build())
                .toList();
        return productQueries;
    }
}