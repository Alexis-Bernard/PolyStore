package fr.polytech.polystore.inventory;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableDiscoveryClient
public class InventoryApplication {

    static final String topicExchangeName = "polystore-exchange";
    static final String queueName = "a-checkout";
    static final String errorQueueName = "b-checkout-rollback";

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    Queue errorQueue() {
        return new Queue(errorQueueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, Queue errorQueue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queueName);
    }

    @Bean
    Binding errorBinding(Queue errorQueue, TopicExchange exchange) {
        return BindingBuilder.bind(errorQueue).to(exchange).with(errorQueueName);
    }

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }

}