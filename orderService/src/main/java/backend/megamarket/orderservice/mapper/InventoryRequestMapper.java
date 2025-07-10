package backend.megamarket.orderservice.mapper;

import client.inventory.response.grpc.InventoryRequestDto;
import client.inventory.response.grpc.ProductQueryDto;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Маппер для преобразования списка продуктов и идентификатора заказа
 * в gRPC DTO {@link InventoryRequestDto}, используемый при отправке запроса в InventoryService.
 * <p>
 * Предназначен для формирования запроса на проверку или изменение остатков товаров на складе.
 */
@Component
@RequiredArgsConstructor
public class InventoryRequestMapper {

    /**
     * Формирует объект {@link InventoryRequestDto} на основе списка товаров и ID заказа.
     *
     * @param productQueries список товаров с количеством для проверки или списания
     * @param orderId        идентификатор заказа
     * @return сформированный gRPC-запрос {@link InventoryRequestDto}
     */
    public InventoryRequestDto map(List<ProductQueryDto> productQueries, Long orderId) {
        InventoryRequestDto request = InventoryRequestDto.newBuilder()
                .setOrderId(orderId)
                .addAllProducts(productQueries)
                .build();
        return request;
    }
}
