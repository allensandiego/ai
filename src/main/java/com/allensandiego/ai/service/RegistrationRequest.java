package com.allensandiego.ai.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for user registration input with validation constraints.
 */
@Data
public class RegistrationRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Roles are required")
    private String roles; // Comma-separated role codes (e.g., "ROLE_USER,ROLE_ADMIN")
}
