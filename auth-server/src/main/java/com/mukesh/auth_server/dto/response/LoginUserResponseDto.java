package com.mukesh.auth_server.dto.response;

import lombok.Builder;

@Builder
public record LoginUserResponseDto(
        Long userId,
        String accessToken,
        String message
) {
}
