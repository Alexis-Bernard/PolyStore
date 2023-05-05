package fr.polytech.polystore.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
class InventoryController {

    private final InventoryRepository inventoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @GetMapping
    public List<Inventory> getAll() {
        logger.info("Getting all inventories");

        return this.inventoryRepository.findAll();
    }

    @PostMapping
    public Inventory updateInventory(@RequestBody Inventory inventory) {
        logger.info("Updating inventory: " + inventory);

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

    @RabbitListener(queues = "a-checkout")
    public void checkout(final String message) {
        logger.info("Checking out: " + message);

        try {
            List<CartItem> cartItems = CartItem.fromJson(message);

            // Check the product quantities
            for (CartItem cartItem : cartItems) {
                Inventory inventory = this.inventoryRepository.findByProductId(cartItem.getId());

                if (inventory.getQuantity() < cartItem.getQuantity()) {
                    throw new Exception("Not enough quantity");
                }
            }

            // Update the inventory
            for (CartItem cartItem : cartItems) {
                Inventory inventory = this.inventoryRepository.findByProductId(cartItem.getId());

                inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
                this.inventoryRepository.save(inventory);
            }

            rabbitTemplate.convertAndSend("polystore-exchange", "b-checkout", CartItem.toJson(cartItems));
        } catch (Exception e) {
            logger.error("Error while checking out: " + e.getMessage());

            rabbitTemplate.convertAndSend("polystore-exchange", "a-checkout-rollback", message);
        }
    }

    @RabbitListener(queues = "b-checkout-rollback")
    public void checkoutRollback(final String message) throws JsonProcessingException {
        logger.info("Rolling back checkout: " + message);

        List<CartItem> cartItems = CartItem.fromJson(message);

        // Rollback the inventory
        for (CartItem cartItem : cartItems) {
            Inventory inventory = this.inventoryRepository.findByProductId(cartItem.getId());

            inventory.setQuantity(inventory.getQuantity() + cartItem.getQuantity());
            this.inventoryRepository.save(inventory);
        }
    }

}