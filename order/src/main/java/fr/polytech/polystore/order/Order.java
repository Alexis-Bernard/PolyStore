package fr.polytech.polystore.order;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@NoArgsConstructor
@Entity(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Long id;

    private Double price;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Item> items;

    public Order(Double price, List<Item> items) {
        this.price = price;
        this.items = items;
    }

}