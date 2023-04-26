package fr.polytech.polystore.gateway;

import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class GatewayController {

    private WebClient webClient;
    private final ReactiveCircuitBreaker readingListCircuitBreaker;

    public GatewayController(WebClient.Builder webClientBuilder, ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = webClientBuilder.baseUrl("lb:").build();
        this.readingListCircuitBreaker = circuitBreakerFactory.create("inventory-service");
    }

    @GetMapping("/products")
    public Flux<GatewayProduct> getProduct() {
        Flux<CatalogProduct> catalogProducts = webClient.get().uri("//catalog-service/products")
                .retrieve()
                .onStatus(status -> status.is5xxServerError(),
                        error -> Mono.error(new RuntimeException("Http 5xx status from catalog service")))
                .bodyToFlux(CatalogProduct.class);

        Flux<InventoryProduct> inventoryProducts = readingListCircuitBreaker.run(
                webClient.get().uri("//inventory-service/inventory").retrieve().bodyToFlux(InventoryProduct.class),
                throwable -> {
                    System.out.println("Http 5xx status from inventory service");
                    return Flux.empty();
                });

        Flux<GatewayProduct> gatewayProducts = catalogProducts
                .flatMap(catalogProduct -> inventoryProducts
                        .filter(inventoryProduct -> inventoryProduct.getProductId().equals(catalogProduct.getId()))
                        .map(inventoryProduct -> new GatewayProduct(catalogProduct.getId(),
                                catalogProduct.getName(),
                                inventoryProduct.getPrice(), inventoryProduct.getQuantity()))
                        .defaultIfEmpty(new GatewayProduct(catalogProduct.getId(),
                                catalogProduct.getName(),
                                -1., -1)));

        return gatewayProducts;
    }

    @PostMapping("/products")
    public Mono<CatalogProduct> createProduct(@RequestBody CatalogProduct product) {
        return webClient.post().uri("//catalog-service/products").body(Mono.just(product), CatalogProduct.class)
                .retrieve().bodyToMono(CatalogProduct.class);
    }

}
