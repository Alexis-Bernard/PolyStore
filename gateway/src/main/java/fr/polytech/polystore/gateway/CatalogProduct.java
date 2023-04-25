package fr.polytech.polystore.gateway;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CatalogProduct {

    private String id;
    private String name;
    private Double price;

    public CatalogProduct(String id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

}
