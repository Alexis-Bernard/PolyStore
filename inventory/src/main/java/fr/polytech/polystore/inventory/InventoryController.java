package fr.polytech.polystore.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
class InventoryController {

    private final InventoryRepository inventoryRepository;
    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private RabbitTemplate rabbitTemplate;

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

    @RabbitListener(queues = "a-checkout")
    public void receiveMessage(final String message) throws JsonProcessingException {
        List<CartItem> cartItems = CartItem.fromJson(message);

        // Update the inventory
        for (CartItem cartItem : cartItems) {
            Inventory inventory = this.inventoryRepository.findByProductId(cartItem.getId());
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            this.inventoryRepository.save(inventory);
        }

        latch.countDown();

        // rabbitTemplate.convertAndSend("polycode-exchange", "a-checkout",
        // CartItem.toJson(cartItems));
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}