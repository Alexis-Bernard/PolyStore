package fr.polytech.polystore.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

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

}