package fr.polytech.polystore.order;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
class OrderController {

    private final OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    Logger logger = LoggerFactory.getLogger(OrderController.class);

    @GetMapping
    public List<Order> getAll() {
        logger.info("Getting all orders");

        return this.orderRepository.findAll();
    }

    @PostMapping
    public Order createShoppingCart(@RequestBody Order order) {
        logger.info("Creating order: " + order);

        return this.orderRepository.save(order);
    }

    @RabbitListener(queues = "b-checkout")
    public void receiveMessage(final String message) throws JsonProcessingException {
        logger.info("Checking out: " + message);

        try {
            List<CartItem> cartItems = CartItem.fromJson(message);

            Double totalPrice = cartItems
                    .stream()
                    .mapToDouble(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                    .sum();

            List<Item> items = cartItems
                    .stream()
                    .map(cartItem -> new Item(cartItem.getId(), cartItem.getQuantity()))
                    .toList();

            this.orderRepository.save(new Order(totalPrice, items));
        } catch (Exception e) {
            logger.error("Error while checking out: " + e.getMessage());

            rabbitTemplate.convertAndSend("polystore-exchange", "b-checkout-rollback", message);
        }
    }

}