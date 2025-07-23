package com.sundeep.user.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Schema(description = "Registered user email", example = "john.doe@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User password", example = "password123")
    @NotBlank(message = "Password is required")
    private String password;
}