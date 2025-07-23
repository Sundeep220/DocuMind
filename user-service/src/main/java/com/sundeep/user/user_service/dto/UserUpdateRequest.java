package com.sundeep.user.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Schema(
            description = "Updated email of the user",
            example = "jane.doe@example.com"
    )
    private String email;

    @Schema(
            description = "New password for the user",
            example = "securePass123"
    )
    private String password;

    @Schema(
            description = "Updated full name of the user",
            example = "Jane Doe"
    )
    private String name;
}
