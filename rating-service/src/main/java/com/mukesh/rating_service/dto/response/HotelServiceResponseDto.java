package com.mukesh.rating_service.dto.response;

import java.time.LocalDateTime;

public record HotelServiceResponseDto(
        Long id,
        String name,
        String location,
        String about,
        String contactNumber,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean active
) {
}
