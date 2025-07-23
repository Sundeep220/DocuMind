package com.sundeep.user.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    @Schema(description = "Generated JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String jwtToken;

    @Schema(description = "Status message", example = "Registration success")
    private String message;

    @Schema(description = "HTTP status as string", example = "OK")
    private String status;
}

