package backend.megamarket.inventoryservice.controller;

import backend.megamarket.inventoryservice.entity.ProductEntity;
import backend.megamarket.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для выполнения CRUD-операций над продуктами.
 * Обрабатывает HTTP-запросы по маршруту "/api/products".
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CrudController {

    /**
     * Сервис для работы с продуктами.
     */
    private final ProductService crudService;

    /**
     * Получить список всех продуктов.
     *
     * @return список продуктов в теле ответа со статусом 200 OK
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        return ResponseEntity.ok(crudService.getAllProducts());
    }

    /**
     * Получить продукт по id.
     *
     * @return список продуктов в теле ответа со статусом 200 OK
     */
    @GetMapping("/products/{product_id}")
    public ResponseEntity<ProductEntity> getProduct(@PathVariable("product_id") Long id) {
        ProductEntity product = crudService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Добавить новый продукт в систему.
     *
     * @param product объект продукта из тела запроса
     * @return добавленный продукт с присвоенным ID (200 OK)
     */
    @PostMapping("/products")
    public ResponseEntity<ProductEntity> addProduct(@RequestBody ProductEntity product) {
        return ResponseEntity.ok(crudService.addProduct(product));
    }

    /**
     * Удалить продукт по его идентификатору.
     *
     * @param id идентификатор продукта
     * @return сообщение об успехе (200 OK) или ошибке (404 Not Found)
     */
    @DeleteMapping("/products/{product_id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("product_id") Long id) {
        if (crudService.deleteProduct(id)) {
            return ResponseEntity.ok("Продукт успешно удален");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Продукт не найден");
        }
    }
}
