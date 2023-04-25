package fr.polytech.polystore.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

@RestController
@RequestMapping("/cart-items")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public List<CartItem> getAllCartItems() {
        return cartService.getCartItems();
    }

    @PostMapping
    public CartItem setCartItem(@RequestBody CartItem cartItem) {
        return cartService.setCartItem(cartItem);
    }

    @PostMapping("/checkout")
    public void checkout() throws JsonProcessingException {
        cartService.checkout();
    }

}