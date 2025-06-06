package backend.megamarket.inventoryService.service.dto.dao;

import backend.megamarket.inventoryService.service.dto.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    Product findByName(String name);
}
