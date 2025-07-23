package com.sundeep.user.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    @Schema(description = "API path where the error occurred", example = "/users")
    private String apiPath;

    @Schema(description = "HTTP status code", example = "400")
    private HttpStatus errorCode;

    @Schema(description = "Detailed error message", example = "Invalid email format")
    private String errorMessage;

    @Schema(description = "Timestamp of the error", example = "2023-08-20T12:34:56")
    private LocalDateTime timestamp;
}
