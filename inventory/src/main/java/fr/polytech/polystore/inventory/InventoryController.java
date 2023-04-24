package fr.polytech.polystore.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
class InventoryController {

    private final InventoryRepository inventoryRepository;

    @GetMapping
    public List<Inventory> getAll() {
        return this.inventoryRepository.findAll();
    }

    @PostMapping
    public Inventory updateInventory(@RequestBody Inventory inventory) {
        // get the inventory
        Inventory inventoryToUpdate = this.inventoryRepository.findByProductId(inventory.getProductId());

        // Return a 404 if the product does not exist
        if (inventoryToUpdate == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found");
        }

        // update the quantity
        inventoryToUpdate.setQuantity(inventory.getQuantity());

        // save the inventory
        return this.inventoryRepository.save(inventoryToUpdate);
    }

}