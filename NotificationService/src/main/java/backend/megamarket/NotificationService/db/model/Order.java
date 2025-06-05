package backend.megamarket.NotificationService.db.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_seq")
    @SequenceGenerator(name = "order_id_seq", sequenceName = "order_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "order_id", nullable = false, unique = true)
    @PrePersist
    public void generateOrderId() {
        this.orderId = System.currentTimeMillis();
    }
    private Long orderId;
    @Column(name="productId", nullable = false)
    private Long productId;
    @Column(name="quantity", nullable = false)
    private Long quantity;
    @Column(name="price", nullable = false)
    private Double price;
    @Column(name="sale")
    private Double sale;
    @Column(name="total_price", nullable = false)
    private Double totalPrice;
    @Column(name="user_id", nullable = false)
    private Long userId;
}