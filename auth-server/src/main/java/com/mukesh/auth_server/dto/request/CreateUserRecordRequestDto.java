package com.mukesh.auth_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRecordRequestDto(
        @NotNull
        Long userId,
        @NotBlank
        String username,
        @NotBlank
        String originalPassword
) {
}
