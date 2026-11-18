package com.mukesh.user_service.dto.request;

import lombok.Builder;

@Builder
public record CreateUserRecordOnAuthServerRequestDto(
        Long userId,
        String username,
        String originalPassword
) {
}
