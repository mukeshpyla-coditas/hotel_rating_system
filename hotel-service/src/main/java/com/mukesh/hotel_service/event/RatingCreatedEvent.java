package com.mukesh.hotel_service.event;

/**
 * hotel-service's OWN copy of the message contract published by rating-service.
 * The two classes live in different packages and projects; only the JSON field names must match.
 */
public record RatingCreatedEvent(
        Long ratingId,
        Long userId,
        Long hotelId,
        Integer rating
) {
}
