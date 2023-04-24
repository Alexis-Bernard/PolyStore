package fr.polytech.polystore.cart;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

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
}