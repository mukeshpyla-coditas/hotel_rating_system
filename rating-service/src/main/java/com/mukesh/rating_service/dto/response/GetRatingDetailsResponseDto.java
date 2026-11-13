package com.mukesh.rating_service.dto.response;

public record GetRatingDetailsResponseDto(
        Long ratingId,
        Integer rating,
        String feedback,
        UserDetailsResponseDto userDetails,
        HotelDetailsResponseDto hotelDetails
) {
}
