package backend.megamarket.inventoryservice.mapper;

import backend.megamarket.inventoryservice.entity.ProductEntity;
import client.inventory_service.response.grpc.InventoryResponseDto;
import client.inventory_service.response.grpc.ProductInfoDto;
import client.inventory_service.response.grpc.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Маппер для преобразования сущностей {@link ProductEntity} в gRPC DTO {@link ProductInfoDto} и {@link InventoryResponseDto}.
 * <p>
 * Используется для формирования ответов от InventoryService, отражающих текущее состояние товаров на складе.
 */
@Component
@RequiredArgsConstructor
public class ProductInfoMapper {

    /**
     * Создает объект {@link ProductInfoDto} с пустыми данными и статусом {@link ProductStatus#INSUFFICIENT_QUANTITY}.
     *
     * @param productId идентификатор отсутствующего товара
     * @return объект {@link ProductInfoDto} с нулевыми значениями и статусом нехватки
     */
    public client.inventory_service.response.grpc.ProductInfoDto emptyMapping(Long productId){
        return client.inventory_service.response.grpc.ProductInfoDto.newBuilder()
                .setProductId(productId)
                .setName("")
                .setPrice(0.0)
                .setDiscount(0.0)
                .setAvailableQuantity(0)
                .setStatus(client.inventory_service.response.grpc.ProductStatus.INSUFFICIENT_QUANTITY)
                .build();
    }

    /**
     * Преобразует сущность товара в объект {@link ProductInfoDto} с заданным статусом.
     *
     * @param product сущность товара из БД
     * @param status  статус товара (например, OK или INSUFFICIENT_QUANTITY)
     * @return объект {@link ProductInfoDto}, описывающий товар
     */
    public client.inventory_service.response.grpc.ProductInfoDto argMapper(ProductEntity product, client.inventory_service.response.grpc.ProductStatus status){
        return client.inventory_service.response.grpc.ProductInfoDto.newBuilder()
                .setProductId(product.getId())
                .setName(product.getName())
                .setPrice(product.getPrice())
                .setDiscount(product.getSale() == null ? 0.0 : product.getSale())
                .setAvailableQuantity(product.getQuantity())
                .setStatus(status)
                .build();
    }

    /**
     * Создает объект {@link InventoryResponseDto} из списка продуктов и ID заказа.
     *
     * @param productInfos список информации о товарах
     * @param orderId      идентификатор заказа
     * @return объект {@link InventoryResponseDto}
     */
    public client.inventory_service.response.grpc.InventoryResponseDto responseMapper(List<ProductInfoDto> productInfos, Long orderId) {
        client.inventory_service.response.grpc.InventoryResponseDto response =
                client.inventory_service.response.grpc.InventoryResponseDto.newBuilder()
                        .setOrderId(orderId)
                        .addAllItems(productInfos)
                        .build();
        return response;
    }

    /**
     * Преобразует товар с недостаточным количеством на складе в {@link ProductInfoDto}.
     *
     * @param productId     идентификатор товара
     * @param product       сущность товара
     * @param currentStock  текущее количество на складе
     * @return DTO с пометкой INSUFFICIENT_QUANTITY
     */
    public  ProductInfoDto productInfoIQMapping(long productId, ProductEntity product, long currentStock){
        return ProductInfoDto.newBuilder()
                .setProductId(productId)
                .setName(product.getName())
                .setPrice(product.getPrice())
                .setDiscount(product.getSale() == null ? 0.0 : product.getSale())
                .setAvailableQuantity(currentStock)
                .setStatus(ProductStatus.INSUFFICIENT_QUANTITY)
                .build();
    }

    /**
     * Преобразует товар с достаточным количеством в {@link ProductInfoDto}.
     *
     * @param productId     идентификатор товара
     * @param product       сущность товара
     * @param currentStock  текущее количество на складе (не используется, сохраняется полное значение)
     * @return DTO со статусом OK
     */
    public  ProductInfoDto productInfoOkMapping(long productId, ProductEntity product, long currentStock) {
        return ProductInfoDto.newBuilder()
                .setProductId(productId)
                .setName(product.getName())
                .setPrice(product.getPrice())
                .setDiscount(product.getSale() == null ? 0.0 : product.getSale())
                .setAvailableQuantity(product.getQuantity())
                .setStatus(ProductStatus.OK)
                .build();
    }
}
