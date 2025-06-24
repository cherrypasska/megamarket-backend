package backend.megamarket.notificationservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность заказа, отображающая таблицу "orders" в базе данных.
 * Хранит данные о заказе.
 */
@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity {

    /**
     * Идентификатор самого заказа (позволяет сгруппировать позиции одного заказа).
     */
    @Id
    @Column(name = "order_id")
    private Long orderId;

    /**
     * Итоговая цена для данной позиции заказа с учетом количества и скидки.
     */
    @Column(name="total_price", nullable = false)
    private Double totalPrice;

    /**
     * Идентификатор пользователя, оформившего заказ.
     */
    @Column(name="user_id", nullable = false)
    private Long userId;
}
