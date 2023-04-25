package fr.polytech.polystore.gateway;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryProduct {

    private Long id;
    private String productId;
    private Integer quantity;

    public InventoryProduct(Long id, String productId, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

}
