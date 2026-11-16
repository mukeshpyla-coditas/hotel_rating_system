package com.mukesh.rating_service.dto.response;

import lombok.Builder;

@Builder
public record HotelDetailsResponseDto(
        String hotelName,
        String location,
        String contactEmail
) {
}
