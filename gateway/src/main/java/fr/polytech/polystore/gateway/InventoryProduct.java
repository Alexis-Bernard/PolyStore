package fr.polytech.polystore.gateway;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryProduct {

    private Long id;
    private String productId;
    private Integer quantity;
    private Double price;

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

}
