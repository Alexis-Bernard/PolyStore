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
    private final String inventoryServiceUrl;
    private final String catalogServiceUrl;

    public GatewayController(
            WebClient.Builder webClientBuilder,
            ReactiveCircuitBreakerFactory circuitBreakerFactory,
            String inventoryServiceUrl,
            String catalogServiceUrl) {
        this.webClient = webClientBuilder.build();
        this.readingListCircuitBreaker = circuitBreakerFactory.create("inventory-service");
        this.inventoryServiceUrl = inventoryServiceUrl;
        this.catalogServiceUrl = catalogServiceUrl;
    }

    @GetMapping("/products")
    public Flux<GatewayProduct> getProduct() {
        Flux<CatalogProduct> catalogProducts = webClient.get().uri(catalogServiceUrl + "/products")
                .retrieve()
                .onStatus(status -> status.is5xxServerError(),
                        error -> Mono.error(new RuntimeException("Http 5xx status from catalog service")))
                .bodyToFlux(CatalogProduct.class);

        Flux<InventoryProduct> inventoryProducts = readingListCircuitBreaker.run(
                webClient.get().uri(inventoryServiceUrl + "/inventory").retrieve().bodyToFlux(InventoryProduct.class),
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
        return webClient.post().uri(catalogServiceUrl + "/products").body(Mono.just(product), CatalogProduct.class)
                .retrieve().bodyToMono(CatalogProduct.class);
    }

}
