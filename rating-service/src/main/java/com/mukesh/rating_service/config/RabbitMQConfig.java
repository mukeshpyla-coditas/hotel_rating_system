package com.mukesh.rating_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RATING_EXCHANGE = "rating.exchange";
    public static final String RATING_CREATED_KEY = "rating.created";

    /**
     * The producer only declares the EXCHANGE. It never knows which queues are bound to it.
     * Declaring is idempotent: RabbitMQ creates it if missing, and does nothing if it already exists.
     */
    @Bean
    public TopicExchange ratingExchange() {
        return new TopicExchange(RATING_EXCHANGE, true, false); // durable, not auto-delete
    }

    /** Send messages as JSON instead of Java serialization. */
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
