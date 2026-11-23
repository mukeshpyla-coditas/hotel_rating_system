package com.mukesh.rating_service.event;

/**
 * Message published when a new rating is saved.
 * It is a plain immutable record, serialized to JSON on the wire.
 * Consumers (e.g. hotel-service) keep their OWN copy of this class, so services stay decoupled.
 */
public record RatingCreatedEvent(
        Long ratingId,
        Long userId,
        Long hotelId,
        Integer rating
) {
}
