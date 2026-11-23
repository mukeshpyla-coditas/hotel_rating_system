package com.mukesh.hotel_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RATING_EXCHANGE = "rating.exchange";
    public static final String RATING_CREATED_KEY = "rating.created";
    public static final String HOTEL_RATING_QUEUE = "hotel.rating.queue";

    /** Same exchange the producer declares. Re-declaring is harmless, and means start-up order doesn't matter. */
    @Bean
    public TopicExchange ratingExchange() {
        return new TopicExchange(RATING_EXCHANGE, true, false);
    }

    /** hotel-service OWNS its queue: durable, so queued messages survive a broker restart. */
    @Bean
    public Queue hotelRatingQueue() {
        return QueueBuilder.durable(HOTEL_RATING_QUEUE).build();
    }

    /** Binding rule: "messages on rating.exchange with key rating.created go into hotel.rating.queue". */
    @Bean
    public Binding hotelRatingBinding(Queue hotelRatingQueue, TopicExchange ratingExchange) {
        return BindingBuilder.bind(hotelRatingQueue).to(ratingExchange).with(RATING_CREATED_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
