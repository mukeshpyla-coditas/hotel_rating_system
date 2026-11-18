package com.mukesh.auth_server.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginUserRequestDto(
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}
