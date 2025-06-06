package backend.megamarket.inventoryService.service;

import backend.megamarket.inventoryService.service.dto.dao.ProductRepository;
import backend.megamarket.inventoryService.service.dto.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public boolean deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.delete(product.get());
            return true;
        }
        return false;
    }

    public Product addProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            Product existing = productRepository.findByName(product.getName());
            existing.setQuantity(existing.getQuantity() + product.getQuantity());
            return productRepository.save(existing);
        } else {
            return productRepository.save(product);
        }
    }

    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){return product.orElse(null);}
        else {return null;}
    }
}
