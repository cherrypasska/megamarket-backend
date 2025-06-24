package backend.megamarket.notificationservice.repository;

import backend.megamarket.notificationservice.entity.OrderEntity;
import backend.megamarket.notificationservice.entity.OrderProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями продуктов в заказе {@link OrderProductsEntity}.
 * Расширяет JpaRepository для предоставления CRUD операций.
 */
@Repository
public interface OrderProductsRepository extends JpaRepository<OrderProductsEntity, Long> {
}
