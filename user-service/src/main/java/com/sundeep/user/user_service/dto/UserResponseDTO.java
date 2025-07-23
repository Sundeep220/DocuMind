package com.sundeep.user.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    @Schema(example = "101")
    private Long id;

    @Schema(example = "John Doe")
    private String name;

    @Schema(example = "john.doe@example.com")
    private String email;

    @Schema(example = "STUDENT")
    private String role;
}