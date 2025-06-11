package backend.megamarket.notificationservice.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность заказа, отображающая таблицу "orders" в базе данных.
 * Хранит данные о конкретном заказе, включая детали продукта, количество и стоимость.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class OrderEntity {

    /**
     * Уникальный идентификатор записи заказа (первичный ключ).
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_seq")
    @SequenceGenerator(name = "order_id_seq", sequenceName = "order_id_seq", allocationSize = 1)
    private Long id;

    /**
     * Идентификатор самого заказа (позволяет сгруппировать позиции одного заказа).
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * Идентификатор продукта, включенного в заказ.
     */
    @Column(name="productId", nullable = false)
    private Long productId;

    /**
     * Количество единиц данного продукта в заказе.
     */
    @Column(name="quantity", nullable = false)
    private Long quantity;

    /**
     * Цена за единицу продукта на момент заказа.
     */
    @Column(name="price", nullable = false)
    private Double price;

    /**
     * Скидка на продукт.
     */
    @Column(name="sale")
    private Double sale;

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