package com.mukesh.api_gateway.dto;

public record ValidateTokenResponse(String username, Long userId, String role) {
}
