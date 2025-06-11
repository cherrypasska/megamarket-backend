package backend.megamarket.inventoryservice.service;

import backend.megamarket.inventoryservice.repository.ProductRepository;
import backend.megamarket.inventoryservice.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для управления товарами на складе.
 * Обеспечивает CRUD-операции над сущностями продуктов.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Возвращает список всех продуктов, хранящихся в базе данных.
     *
     * @return список всех {@link ProductEntity}
     */
    @Override
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Удаляет продукт по его идентификатору, если он существует.
     *
     * @param id идентификатор продукта
     * @return true, если продукт был успешно удалён; false, если продукт не найден
     */
    @Override
    public boolean deleteProduct(Long id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.delete(product.get());
            return true;
        }
        return false;
    }

    /**
     * Добавляет новый продукт или увеличивает количество уже существующего с таким же названием.
     *
     * @param product сущность {@link ProductEntity}, которую необходимо добавить
     * @return сохранённая или обновлённая сущность {@link ProductEntity}
     */
    @Override
    public ProductEntity addProduct(ProductEntity product) {
        if (productRepository.existsByName(product.getName())) {
            ProductEntity existing = productRepository.findByName(product.getName());
            existing.setQuantity(existing.getQuantity() + product.getQuantity());
            return productRepository.save(existing);
        } else {
            return productRepository.save(product);
        }
    }

    /**
     * Возвращает продукт по его идентификатору.
     *
     * @param id идентификатор продукта
     * @return {@link ProductEntity}, если найден; иначе null
     */
    @Override
    public ProductEntity getProductById(Long id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            return product.orElse(null);
        } else {
            return null;
        }
    }
}
