package com.mukesh.rating_service.dto.response;

import lombok.Builder;

@Builder
public record UserDetailsResponseDto(
        String userName,
        String contactNumber,
        String email
) {
}
