package com.sundeep.user.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserPatchRequest {

    @Schema(
            description = "Field to update: 'name', 'email', or 'password'",
            example = "name"
    )
    private String field;

    @Schema(
            description = "New value for the specified field",
            example = "Jane Smith"
    )
    private String value;
}
