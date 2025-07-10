package backend.megamarket.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity продукт на складе.
 */
@Entity
@NoArgsConstructor
@Table(name = "products")
@Data
public class ProductEntity {

    /**
     * Уникальный идентификатор продукта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название продукта (уникальное и обязательное).
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Количество продукта на складе.
     */
    @Column
    private Long quantity;

    /**
     * Цена продукта.
     */
    @Column(nullable = false)
    private Double price;

    /**
     * Скидка на продукт.
     */
    @Column
    private Double sale;
}
