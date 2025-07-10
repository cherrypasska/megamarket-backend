package backend.megamarket.inventoryservice.service;

import backend.megamarket.inventoryservice.dto.ProductDto;
import backend.megamarket.inventoryservice.entity.ProductEntity;

import java.util.List;

public interface ProductService {

    List<ProductEntity> getAllProducts();

    boolean deleteProduct(Long id);

    ProductEntity addProduct(ProductDto product);

    ProductEntity getProductById(Long id);
}
