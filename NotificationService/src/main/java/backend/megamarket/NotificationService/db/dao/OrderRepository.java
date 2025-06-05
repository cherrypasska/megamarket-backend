package backend.megamarket.NotificationService.db.dao;

import backend.megamarket.NotificationService.db.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderId(Long orderId);
    List<Order> findByUserId(Long userId);
}