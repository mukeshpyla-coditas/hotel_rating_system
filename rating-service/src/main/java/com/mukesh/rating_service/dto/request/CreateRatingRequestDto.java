package com.mukesh.rating_service.dto.request;

import jakarta.validation.constraints.*;

public record CreateRatingRequestDto(
        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be a positive number")
        Long userId,

        @NotNull(message = "Hotel ID is required")
        @Positive(message = "Hotel ID must be a positive number")
        Long hotelId,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating cannot exceed 5")
        Integer rating,

        @Size(max = 1000, message = "Feedback cannot exceed 1000 characters")
        String feedback
) {
}
