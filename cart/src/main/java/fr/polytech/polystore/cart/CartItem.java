package fr.polytech.polystore.cart;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@RedisHash("CartItem")
public class CartItem implements Serializable {
    private String id;
    private Integer quantity;

    public String getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    static String toJson(List<CartItem> cartItems) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(cartItems);
    }
}