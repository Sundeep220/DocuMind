package com.sundeep.user.user_service.controller;

import com.sundeep.user.user_service.dto.*;
import com.sundeep.user.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Tag(
        name = "CRUD REST APIs for User Management",
        description = "CRUD REST APIs for User Service"
)
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserServiceInfoDTO userServiceInfoDTO;

    @Operation(
            summary = "Get User Profile",
            description = """
        Fetches the profile of the currently authenticated user using the JWT token provided in the Authorization header.
        The email is extracted from the token to retrieve the user record.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUserProfile(@RequestHeader("Authorization") String jwtToken) {
        return new ResponseEntity<>(userService.getUserProfile(jwtToken), HttpStatus.OK);
    }


    @Operation(
            summary = "Get User by ID",
            description = "Fetch a user's details by ID. Requires valid JWT authentication."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User details fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID of the user to fetch", example = "101")
            @PathVariable Long id,
            Authentication auth )
    {
        String requesterEmail = auth.getName();
        System.out.println("Authentication Object:" + auth.toString());
        return ResponseEntity.ok(userService.getUserById(id, requesterEmail));
    }

    @Operation(
            summary = "Get user by email",
            description = """
            Retrieves user profile details using their email address.
            Accessible by the user themselves or an ADMIN role.
            Requires a valid JWT token.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User details fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Access denied (neither ADMIN nor the user themselves)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found with the provided email",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @Valid
            @Parameter(
                    description = "Email of the user",
                    example = "john.doe@example.com"
            )
            @RequestParam
            @Pattern(
                    regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
                    message = "Invalid email format"
            )
            String email,
            Authentication auth
    ) {
        return ResponseEntity.ok(userService.getUserByEmail(email, auth.getName()));
    }

    @Operation(
            summary = "Update user details by ID",
            description = """
        Updates a user's name, email, and password based on their ID.
        Accessible only by the user themselves or an ADMIN.
        Requires valid JWT token in the Authorization header.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., malformed email or missing fields)",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not allowed to update this user",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "User not found with the given ID", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            ))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to update", example = "101")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(  // another annotation but from swagger
                description = "User details to update: email, name, and password",
                required = true,
                content = @Content(
                        schema = @Schema(implementation = UserUpdateRequest.class)
                )
            )
            @Valid @RequestBody UserUpdateRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(userService.updateUserDetails(id, auth.getName(), request));
    }

    @Operation(
            summary = "Patch user by ID",
            description = """
        Partially updates a user's name, email, or password.
        Only the user themselves or an ADMIN can perform this operation.
        Requires a valid JWT token.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid field or input",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access denied",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            ))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> patchUser(
            @Parameter(description = "ID of the user to patch", example = "101")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Patch request specifying the field and new value",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserPatchRequest.class))
            )
            @RequestBody UserPatchRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(userService.patchUser(id, auth.getName(), request));
    }

    @Operation(
            summary = "Delete user by ID",
            description = """
        Deletes a user by ID. Only accessible by the user themselves or an ADMIN.
        Requires a valid JWT token for authorization.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(example = "User deleted successfully.")
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access denied",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            ))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "ID of the user to delete", example = "101")
            @PathVariable Long id,
            Authentication auth) {
        userService.deleteUserById(id, auth.getName());
        return ResponseEntity.ok("User deleted successfully.");
    }

    @Operation(
            summary = "Get all users (paginated, admin-only)",
            description = """
        Returns a paginated list of all users in the system.
        Only accessible by users with the ADMIN role.
        You can optionally filter by user role using the `role` query parameter.
        Requires a valid JWT token in the Authorization header.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of users fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PageOfUserResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid role or pagination parameter", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT missing or invalid",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only admins allowed",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            ))
    })
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @Parameter(description = "Page number (0-based index)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (number of users per page)", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Filter users by role (STUDENT, INSTRUCTOR, ADMIN)", example = "STUDENT")
            @RequestParam(required = false) String role,

            Authentication auth
    ) {
        return ResponseEntity.ok(userService.getAllUsers(auth.getName(), page, size, role));
    }

    @Operation(
            summary = "Get user service info",
            description = "Returns metadata about the User Service such as version, description, and service name. Public endpoint; no authentication required."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service information fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserServiceInfoDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("service-info")
    public ResponseEntity<UserServiceInfoDTO> getServiceInfo() {
        return ResponseEntity.ok(userServiceInfoDTO);
    }

}
