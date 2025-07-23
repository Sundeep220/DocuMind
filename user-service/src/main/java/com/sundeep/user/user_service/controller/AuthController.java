package com.sundeep.user.user_service.controller;

import com.sundeep.user.user_service.dto.AuthResponse;
import com.sundeep.user.user_service.dto.ErrorResponseDTO;
import com.sundeep.user.user_service.dto.LoginRequest;
import com.sundeep.user.user_service.dto.UserRequestDTO;
import com.sundeep.user.user_service.entity.User;
import com.sundeep.user.user_service.exceptions.EmailAlreadyExistsException;
import com.sundeep.user.user_service.exceptions.UserNotFoundException;
import com.sundeep.user.user_service.repo.UserRepository;
import com.sundeep.user.user_service.service.impl.CustomUserServiceImpl;
import com.sundeep.user.user_service.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "APIs for user authentication and registration",
        description = "APIs for user authentication and registration for user service"
)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserServiceImpl userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(
            summary = "Register a new user",
            description = """
        Registers a new user with name, email, password, and role (STUDENT, INSTRUCTOR, ADMIN).
        Returns a JWT token upon successful registration.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
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
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRequestDTO userRequest) throws Exception {
    // TODO: Add validations here (email uniqueness, role validation, password hashing)
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        String name = userRequest.getName();
        String role = userRequest.getRole();

        userRepository.findByEmail(userRequest.getEmail()).ifPresent(u -> {
            throw new EmailAlreadyExistsException("Email already registered: " + userRequest.getEmail());
        });

        // Hash password
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .role(User.Role.valueOf(role.toUpperCase()))
                .build();

        User savedUser = userRepository.save(user);
        // Create an authentication object for a saved user and set it in security context holder for authentication
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

//        Authentication auth = new UsernamePasswordAuthenticationToken(email, password, authorities);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        // Generate JWT token and return response
        String jwtToken = JwtUtil.generateTokenAuthentication(auth);
        AuthResponse authResponse = new AuthResponse(jwtToken, "Registration success", String.valueOf(HttpStatus.OK));
        return new ResponseEntity<>(authResponse, HttpStatus.OK);

    }

    @Operation(
            summary = "Login user",
            description = """
        Authenticates a user using email and password and returns a JWT token upon success.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful. JWT returned.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid email or password",
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
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> userLogin(@RequestBody LoginRequest user) throws Exception {
        String email = user.getEmail();
        String password = user.getPassword();

        userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email));

        // Authenticate user and set it in security context holder
//        Authentication authentication = authenticate(email, password);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token and return response
        String jwtToken = JwtUtil.generateTokenAuthentication(authentication);
        AuthResponse authResponse = new AuthResponse(jwtToken, "Login success", String.valueOf(HttpStatus.OK));
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

}
