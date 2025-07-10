package backend.megamarket.inventoryservice.mapper;

import backend.megamarket.inventoryservice.dto.ProductDto;
import backend.megamarket.inventoryservice.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Компонент, выполняющий преобразование объекта {@link ProductDto}
 * в сущность {@link ProductEntity}, используемую для хранения в базе данных.
 * <p>
 * Используется при сохранении или обновлении продукта на основе данных,
 * полученных от клиента.
 */
@RequiredArgsConstructor
@Component
public class ProductDtoToEntityMapper {

    /**
     * Преобразует DTO продукта в сущность продукта.
     *
     * @param productDto DTO продукта, содержащий данные от клиента
     * @return сущность продукта для сохранения в базе данных
     */
    public ProductEntity map(ProductDto productDto) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setQuantity(productDto.getQuantity());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setName(productDto.getName());
        productEntity.setSale(productDto.getSale());
        return productEntity;
    }
}
