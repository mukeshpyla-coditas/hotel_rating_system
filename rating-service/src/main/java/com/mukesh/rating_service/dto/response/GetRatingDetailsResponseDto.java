package com.mukesh.rating_service.dto.response;

import lombok.Builder;

@Builder
public record GetRatingDetailsResponseDto(
        Long ratingId,
        Integer rating,
        String feedback,
        UserDetailsResponseDto userDetails,
        HotelDetailsResponseDto hotelDetails
) {
}
