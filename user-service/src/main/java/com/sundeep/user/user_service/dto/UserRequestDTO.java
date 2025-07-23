package com.sundeep.user.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {

    @Schema(description = "Full name of the user", example = "John Doe")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Password (min 6 characters)", example = "strongPass123")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Schema(description = "Role of the user (STUDENT, INSTRUCTOR, ADMIN)", example = "STUDENT")
    @NotBlank(message = "Role is required")
    private String role;
}
