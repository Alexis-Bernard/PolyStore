package fr.polytech.polystore.cart;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItemRepository.findAll().forEach(cartItems::add);
        return cartItems;
    }

    public CartItem setCartItem(CartItem cartItem) {
        if (cartItem.getQuantity() == 0) {
            cartItemRepository.delete(cartItem);

            return null;
        } else {
            return cartItemRepository.save(cartItem);
        }
    }

    public void checkout() {
        List<CartItem> cartItems = getCartItems();

        rabbitTemplate.convertAndSend("polycode-exchange", "a-checkout", "test");

        for (CartItem cartItem : cartItems) {
            // cartItemRepository.delete(cartItem);
        }
    }

}