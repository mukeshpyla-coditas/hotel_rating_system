
package com.mukesh.user_service.dto.request;

import jakarta.validation.constraints.*;

public record CreateUserRequestDto(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Size(max = 50, message = "Middle name cannot exceed 50 characters")
        String middleName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,

        @NotBlank(message = "Contact number is required")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Contact number must contain 10 to 15 digits, optionally prefixed with +"
        )
        String contactNumber,

        @Size(max = 500, message = "About section cannot exceed 500 characters")
        String about

) {}
