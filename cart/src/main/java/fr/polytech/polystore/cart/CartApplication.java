package fr.polytech.polystore.cart;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
public class CartApplication {

    static final String topicExchangeName = "polystore-exchange";
    static final String errorQueueName = "a-checkout-rollback";

    @Bean
    Queue errorQueue() {
        return new Queue(errorQueueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding errorBinding(Queue errorQueue, TopicExchange exchange) {
        return BindingBuilder.bind(errorQueue).to(exchange).with(errorQueueName);
    }

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }

}
