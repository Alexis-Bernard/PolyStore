package fr.polytech.polystore.order;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

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

    @RabbitListener(queues = "b-checkout")
    public void receiveMessage(final String message) throws JsonProcessingException {
        List<CartItem> cartItems = CartItem.fromJson(message);

        // Create the order
        for (CartItem cartItem : cartItems) {
            System.out.println("Ordering " + cartItem.getQuantity() + " " + cartItem.getId() + " for "
                    + cartItem.getPrice() + "€ each");
            // Inventory inventory =
            // this.inventoryRepository.findByProductId(cartItem.getId());
            // inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            // this.inventoryRepository.save(inventory);
        }

        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}