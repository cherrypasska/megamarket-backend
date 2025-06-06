package backend.megamarket.service.orderService.controllers;

import backend.megamarket.service.orderService.dbs.dao.OrderItem;
import backend.megamarket.service.orderService.servise.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody List<OrderItem> items) {
        orderService.сheckOrder(items);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}