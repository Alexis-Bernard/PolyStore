package fr.polytech.polystore.inventory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItem implements Serializable {
    private String id;
    private Integer quantity;
    private Double price;

    public String getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    static String toJson(List<CartItem> cartItems) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(cartItems);
    }

    static public List<CartItem> fromJson(String json) throws JsonProcessingException {
        return Arrays.asList(new ObjectMapper().readValue(json, CartItem[].class));
    }
}