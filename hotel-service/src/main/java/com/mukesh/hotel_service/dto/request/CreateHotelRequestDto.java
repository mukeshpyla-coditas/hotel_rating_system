package com.mukesh.hotel_service.dto.request;

import jakarta.validation.constraints.*;

public record CreateHotelRequestDto(

        @NotBlank(message = "Hotel name is required")
        @Size(min = 2, max = 100, message = "Hotel name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Location is required")
        @Size(max = 500, message = "Location cannot exceed 500 characters")
        String location,

        @Size(max = 1000, message = "About section cannot exceed 1000 characters")
        String about,

        @NotBlank(message = "Contact number is required")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Contact number must contain 10 to 15 digits, optionally prefixed with +"
        )
        String contactNumber,

        @Email(message = "Please provide a valid email address")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email

) {}
