package backend.megamarket.inventoryService.service.controller;

import backend.megamarket.inventoryService.service.CrudService;
import backend.megamarket.inventoryService.service.db.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CrudController {
    private final CrudService crudService;

    public CrudController(CrudService crudService) {
        this.crudService = crudService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(crudService.getAllProducts());
    }

    @GetMapping("/products/{product_id}")
    public ResponseEntity<Product> getProduct(@PathVariable("product_id") Long id) {
        Product product = crudService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(crudService.addProduct(product));
    }

    @DeleteMapping("/products/{product_id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("product_id")Long id) {
        if(crudService.deleteProduct(id)){
            return ResponseEntity.ok("Продукт успешно удален");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Продукт не найден");
        }
    }
}
