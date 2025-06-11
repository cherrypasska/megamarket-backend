package backend.megamarket.inventoryservice.service;

import backend.megamarket.inventoryservice.entity.ProductEntity;

import java.util.List;

public interface ProductService {
    List<ProductEntity> getAllProducts();

    boolean deleteProduct(Long id);

    ProductEntity addProduct(ProductEntity product);

    ProductEntity getProductById(Long id);
}
