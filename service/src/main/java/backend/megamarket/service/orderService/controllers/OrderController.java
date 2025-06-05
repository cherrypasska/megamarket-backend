package backend.megamarket.service.controllers;

import backend.megamarket.service.controllers.model.OrderRequest;
import backend.megamarket.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api")
public class OrderController {

    private OrderService orderService = new OrderService();
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request/*, @RequestHeader("Authorization") String jwtToken*/) {
        System.out.println("Received: " + request);
        return ResponseEntity.ok("Order received: " + request.getProductId() + " x" + request.getQuantity());
    }
}