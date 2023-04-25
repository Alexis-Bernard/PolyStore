package fr.polytech.polystore.cart;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

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

    @Override
    public String toString() {
        return "CartItem{" +
                "id='" + id + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}