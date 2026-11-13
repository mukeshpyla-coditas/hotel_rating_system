package com.mukesh.rating_service.dto.response;

public record HotelDetailsResponseDto(
        String hotelName,
        String location,
        String contactEmail
) {
}
