package backend.megamarket.NotificationService.controller;

import backend.megamarket.NotificationService.Service.OrderService;
import backend.megamarket.NotificationService.Service.impl.OrderServiceImpl;
import backend.megamarket.NotificationService.db.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllProducts());
    }
    @GetMapping("/{order_id}")
    public ResponseEntity<List<Order>> getOrderById(@PathVariable Long order_id) {
        List<Order> orders = orderService.getOrdersByOrderId(order_id);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<List<Order>> getOrderByUserId(@PathVariable Long user_id) {
        List<Order> orders = orderService.getOrdersByUserId(user_id);
        return ResponseEntity.ok(orders);
    }
}