package backend.megamarket.inventoryService.service.db.dao;

import backend.megamarket.inventoryService.service.db.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    Product findByName(String name);
}
