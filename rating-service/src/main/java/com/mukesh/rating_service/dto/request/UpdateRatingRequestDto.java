package com.mukesh.rating_service.dto.request;

import jakarta.validation.constraints.*;

public record UpdateRatingRequestDto(
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating cannot exceed 5")
        Integer rating,

        @Size(max = 1000, message = "Feedback cannot exceed 1000 characters")
        String feedback

) {}

