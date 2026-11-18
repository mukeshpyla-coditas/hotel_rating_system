package com.mukesh.auth_server.dto.response;

import lombok.Builder;

@Builder
public record ValidateTokenResponseDto(
        String username,
        Long userId,
        String role
) {
}
