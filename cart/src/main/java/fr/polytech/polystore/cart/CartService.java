package fr.polytech.polystore.cart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    Logger logger = LoggerFactory.getLogger(CartService.class);

    public List<CartItem> getCartItems() {
        logger.info("Getting cart items");

        List<CartItem> cartItems = new ArrayList<>();
        cartItemRepository.findAll().forEach(cartItems::add);
        return cartItems;
    }

    public CartItem setCartItem(CartItem cartItem) {
        logger.info("Setting cart item: " + cartItem);

        if (cartItem.getQuantity() == 0) {
            cartItemRepository.delete(cartItem);

            return null;
        } else {
            return cartItemRepository.save(cartItem);
        }
    }

    public void checkout() throws JsonProcessingException {
        List<CartItem> cartItems = getCartItems();

        logger.info("Checking out: " + CartItem.toJson(cartItems));

        rabbitTemplate.convertAndSend("polystore-exchange", "a-checkout", CartItem.toJson(cartItems));

        for (CartItem cartItem : cartItems) {
            cartItemRepository.delete(cartItem);
        }
    }

    @RabbitListener(queues = "a-checkout-rollback")
    public void checkoutRollback(final String message) throws JsonProcessingException {
        logger.info("Checking out rollback: " + message);

        List<CartItem> cartItems = CartItem.fromJson(message);

        for (CartItem cartItem : cartItems) {
            Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItem.getId());

            if (cartItemOptional.isPresent()) {
                CartItem cartItemToUpdate = cartItemOptional.get();
                cartItemToUpdate.setQuantity(cartItemToUpdate.getQuantity() + cartItem.getQuantity());
                cartItemRepository.save(cartItemToUpdate);
            } else {
                cartItemRepository.save(cartItem);
            }
        }
    }

}