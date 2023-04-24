package fr.polytech.polystore.inventory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Inventory save(Inventory inventory);

    List<Inventory> findAll();

    Inventory findByProductId(String productId);

}