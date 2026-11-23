package com.mukesh.rating_service.messaging;

import com.mukesh.rating_service.config.RabbitMQConfig;
import com.mukesh.rating_service.event.RatingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishRatingCreated(RatingCreatedEvent event) {
        try {
            // exchange, routing key, payload (converted to JSON by our MessageConverter bean)
            rabbitTemplate.convertAndSend(RabbitMQConfig.RATING_EXCHANGE, RabbitMQConfig.RATING_CREATED_KEY, event);
            log.info("Published RatingCreatedEvent for rating-{} to exchange '{}' with key '{}'",
                    event.ratingId(), RabbitMQConfig.RATING_EXCHANGE, RabbitMQConfig.RATING_CREATED_KEY);
        } catch (AmqpException ex) {
            // The rating is already saved, so don't fail the user's request because the broker is down.
            // NOTE: this event is now LOST. Fixing that properly (outbox pattern) comes in the reliability step.
            log.error("Could not publish RatingCreatedEvent for rating-{}: {}", event.ratingId(), ex.getMessage());
        }
    }
}
