package fr.polytech.polystore.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
class OrderController {

    private final OrderRepository orderRepository;

    @GetMapping
    public List<Order> getAll() {
        return this.orderRepository.findAll();
    }

    @PostMapping
    public Order createShoppingCart(@RequestBody Order order) {
        return this.orderRepository.save(order);
    }

}