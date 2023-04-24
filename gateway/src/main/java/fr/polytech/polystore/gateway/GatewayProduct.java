package fr.polytech.polystore.gateway;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GatewayProduct {

    private String id;
    private String name;
    private Double price;
    private Integer quantity;

    public GatewayProduct(String id, String name, Double price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "GatewayProduct {id=" + id + ", name=" + name + ", price=" + price + ", quantity=" + quantity + "}";
    }

}
