package com.sundeep.user.user_service.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "PageOfUserResponseDTO", description = "Paginated result of users")
public class PageOfUserResponseDTO {

    @Schema(description = "Current page number", example = "0")
    private int number;

    @Schema(description = "Number of items per page", example = "10")
    private int size;

    @Schema(description = "Total number of users", example = "200")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "20")
    private int totalPages;

    @Schema(description = "Is this the last page?", example = "false")
    private boolean last;

    @ArraySchema(
            schema = @Schema(implementation = UserResponseDTO.class),
            arraySchema = @Schema(description = "List of user records")
    )
    private List<UserResponseDTO> content;
}
