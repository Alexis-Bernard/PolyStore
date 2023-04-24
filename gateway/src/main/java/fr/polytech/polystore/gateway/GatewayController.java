package fr.polytech.polystore.gateway;

import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class GatewayController {

    private WebClient webClient;
    private final ReactiveCircuitBreaker readingListCircuitBreaker;

    public GatewayController(WebClient.Builder webClientBuilder, ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = webClientBuilder.build();
        this.readingListCircuitBreaker = circuitBreakerFactory.create("inventory-service");
    }

    @GetMapping("/products")
    public Flux<GatewayProduct> getProduct() {
        Flux<CatalogProduct> catalogProducts = webClient.get().uri("lb://catalog-service/products")
                .retrieve()
                .onStatus(status -> status.is5xxServerError(),
                        error -> Mono.error(new RuntimeException("Http 5xx status from catalog service")))
                .bodyToFlux(CatalogProduct.class);

        Flux<InventoryProduct> inventoryProducts = readingListCircuitBreaker.run(
                webClient.get().uri("lb://inventory-service/inventory").retrieve().bodyToFlux(InventoryProduct.class),
                throwable -> {
                    System.out.println("Http 5xx status from inventory service");
                    return Flux.empty();
                });

        Flux<GatewayProduct> gatewayProducts = catalogProducts
                .flatMap(catalogProduct -> inventoryProducts
                        .filter(inventoryProduct -> inventoryProduct.getProductId().equals(catalogProduct.getId()))
                        .map(inventoryProduct -> new GatewayProduct(catalogProduct.getId(),
                                catalogProduct.getName(),
                                catalogProduct.getPrice(), inventoryProduct.getQuantity()))
                        .defaultIfEmpty(new GatewayProduct(catalogProduct.getId(),
                                catalogProduct.getName(),
                                catalogProduct.getPrice(), -1)));

        return gatewayProducts;
    }

}
