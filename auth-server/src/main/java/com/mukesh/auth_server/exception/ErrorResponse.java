package com.mukesh.auth_server.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        Integer status,
        String message,
        LocalDateTime createdAt
) {
}
