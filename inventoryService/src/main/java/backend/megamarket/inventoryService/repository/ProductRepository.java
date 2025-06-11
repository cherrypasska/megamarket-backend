package backend.megamarket.inventoryservice.repository;

import backend.megamarket.inventoryservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для управления сущностями {@link ProductEntity}.
 * Предоставляет стандартные CRUD-операции, а также методы поиска по имени.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * Проверяет, существует ли продукт с указанным именем.
     *
     * @param name имя продукта
     * @return true, если продукт с таким именем существует, иначе false
     */
    boolean existsByName(String name);

    /**
     * Ищет продукт по имени.
     *
     * @param name имя продукта
     * @return найденная сущность {@link ProductEntity}, либо null, если продукт не найден
     */
    ProductEntity findByName(String name);
}