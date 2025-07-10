package backend.megamarket.inventoryservice.mapper;

import client.inventory_service.response.grpc.InventoryResponseDto;
import client.inventory_service.response.grpc.ProductInfoDto;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Маппер для создания {@link InventoryResponseDto}, содержащего информацию об обновленных товарах на складе.
 * <p>
 * Используется для формирования ответа после выполнения заказа,
 * в который включаются все позиции товаров с их актуальными статусами.
 */
@Component
@RequiredArgsConstructor
public class InventoryResponseMapper {

    /**
     * Преобразует список {@link ProductInfoDto} и идентификатор заказа в {@link InventoryResponseDto}.
     *
     * @param orderId         идентификатор заказа
     * @param updatedProducts список товаров с обновленным статусом на складе
     * @return объект {@link InventoryResponseDto}, содержащий ID заказа и список товаров
     */
    public InventoryResponseDto InventoryResponseMapping(Long orderId, List<ProductInfoDto> updatedProducts) {
        InventoryResponseDto response = InventoryResponseDto.newBuilder()
                .setOrderId(orderId)
                .addAllItems(updatedProducts)
                .build();
        return response;
    }
}
