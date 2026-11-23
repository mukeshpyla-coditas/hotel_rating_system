package com.mukesh.hotel_service.messaging;

import com.mukesh.hotel_service.config.RabbitMQConfig;
import com.mukesh.hotel_service.event.RatingCreatedEvent;
import com.mukesh.hotel_service.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingEventListener {

    private final HotelRepository hotelRepository;

    /**
     * Spring calls this method for every message that lands in hotel.rating.queue.
     * The JSON body is converted into RatingCreatedEvent using the method's parameter type.
     * If the method returns normally, Spring sends an ACK and RabbitMQ deletes the message.
     * If it throws, the message is NOT acked.
     */
    @RabbitListener(queues = RabbitMQConfig.HOTEL_RATING_QUEUE)
    @Transactional
    public void onRatingCreated(RatingCreatedEvent event) {
        log.info("Received RatingCreatedEvent: rating-{} gave {} stars to hotel-{}",
                event.ratingId(), event.rating(), event.hotelId());

        int updated = hotelRepository.applyNewRating(event.hotelId(), event.rating());

        if (updated == 0) {
            // Deliberately not throwing: a hotel that doesn't exist will never exist on retry,
            // and throwing would make RabbitMQ redeliver the same message forever.
            log.warn("Hotel-{} not found, dropping event for rating-{}", event.hotelId(), event.ratingId());
        } else {
            log.info("Updated average rating of hotel-{}", event.hotelId());
        }
    }
}
