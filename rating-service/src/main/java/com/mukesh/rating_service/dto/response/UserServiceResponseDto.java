package com.mukesh.rating_service.dto.response;

import java.time.LocalDateTime;

public record UserServiceResponseDto(
        Long id,
        String firstName,
        String middleName,
        String lastName,
        String email,
        String contactNumber,
        String about,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean active
) {
}
