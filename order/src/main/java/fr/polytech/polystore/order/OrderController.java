package fr.polytech.polystore.order;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
class OrderController {

    private final OrderRepository orderRepository;
    private CountDownLatch latch = new CountDownLatch(1);

    @GetMapping
    public List<Order> getAll() {
        return this.orderRepository.findAll();
    }

    @PostMapping
    public Order createShoppingCart(@RequestBody Order order) {
        return this.orderRepository.save(order);
    }

    @RabbitListener(queues = "a-checkout")
    public void receiveMessage(final String message) {
        System.out.println("Received <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}