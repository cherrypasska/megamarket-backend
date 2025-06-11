package backend.megamarket.service.orderservice.controllers;

import backend.megamarket.service.orderservice.dtos.OrderItemDto;
import backend.megamarket.service.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для обработки заказов.
 * <p>
 * Отвечает за приём новых заказов и их первичную валидацию.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Создаёт новый заказ на основе списка товаров.
     * <p>
     * Производит валидацию заказа, включая проверку на наличие товаров и их количество.
     *
     * @param items список позиций заказа, передаваемых в теле запроса
     * @return HTTP 201 (Created) в случае успешной проверки
     */
    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody List<OrderItemDto> items) {
        orderService.checkOrder(items);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
