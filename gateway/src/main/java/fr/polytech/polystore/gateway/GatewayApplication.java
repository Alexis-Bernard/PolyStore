package fr.polytech.polystore.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

    @Bean
    public String inventoryServiceUrl(@Value("${services.inventory-service.url}") String inventoryServiceUrl) {
        return inventoryServiceUrl;
    }

    @Bean
    public String catalogServiceUrl(@Value("${services.catalog-service.url}") String catalogServiceUrl) {
        return catalogServiceUrl;
    }

    @Bean
    public String cartServiceUrl(@Value("${services.cart-service.url}") String cartServiceUrl) {
        return cartServiceUrl;
    }

    @Bean
    public String orderServiceUrl(@Value("${services.order-service.url}") String orderServiceUrl) {
        return orderServiceUrl;
    }

    @Bean
    // @LoadBalanced is desactivated because of an error if eureka is not enabled
    // @LoadBalanced
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public RouteLocator myRoutes(
            RouteLocatorBuilder builder,
            String catalogServiceUrl,
            String cartServiceUrl,
            String orderServiceUrl) {
        return builder.routes()
                .route(p -> p
                        .path("/products/**")
                        .uri(catalogServiceUrl))
                .route(p -> p
                        .path("/cart-items/**")
                        .uri(cartServiceUrl))
                .route(p -> p
                        .path("/orders/**")
                        .uri(orderServiceUrl))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
