package backend.megamarket.notificationservice.controller;

import backend.megamarket.notificationservice.service.OrderService;
import backend.megamarket.notificationservice.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST контроллер для управления заказами.
 * <p>
 * Предоставляет эндпоинты для получения всех заказов,
 * а также заказов по идентификатору заказа и пользователю.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Получить список всех заказов.
     *
     * @return HTTP ответ с кодом 200 и списком всех {@link OrderEntity}
     */
    @GetMapping("/all")
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllProducts());
    }

    /**
     * Получить заказ по идентификатору заказа.
     *
     * @param order_id идентификатор заказа
     * @return HTTP ответ с кодом 200 и списком заказов с заданным ID
     */
    @GetMapping("/{order_id}")
    public ResponseEntity<List<OrderEntity>> getOrderById(@PathVariable Long order_id) {
        List<OrderEntity> orders = orderService.getOrdersByOrderId(order_id);
        return ResponseEntity.ok(orders);
    }

    /**
     * Получить заказы по идентификатору пользователя.
     *
     * @param user_id идентификатор пользователя
     * @return HTTP ответ с кодом 200 и списком заказов пользователя
     */
    @GetMapping("/{user_id}")
    public ResponseEntity<List<OrderEntity>> getOrderByUserId(@PathVariable Long user_id) {
        List<OrderEntity> orders = orderService.getOrdersByUserId(user_id);
        return ResponseEntity.ok(orders);
    }
}