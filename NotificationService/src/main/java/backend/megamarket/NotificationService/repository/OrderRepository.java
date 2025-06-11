package backend.megamarket.notificationservice.repository;

import backend.megamarket.notificationservice.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностями заказов {@link OrderEntity}.
 * Расширяет JpaRepository для предоставления CRUD операций и дополнительных методов поиска.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    /**
     * Находит список заказов по идентификатору заказа.
     *
     * @param orderId идентификатор заказа
     * @return список сущностей {@link OrderEntity} с заданным orderId
     */
    List<OrderEntity> findByOrderId(Long orderId);

    /**
     * Находит список заказов по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список сущностей {@link OrderEntity} с заданным userId
     */
    List<OrderEntity> findByUserId(Long userId);
}