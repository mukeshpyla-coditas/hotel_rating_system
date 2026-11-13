package com.mukesh.rating_service.dto.response;

public record UserDetailsResponseDto(
        String userName,
        String contactNumber,
        String email
) {
}
