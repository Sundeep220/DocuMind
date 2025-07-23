package com.sundeep.user.user_service.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        // Will apply these filters later
                        // Allow only Admin role for DELETE /api/users/**
                        .requestMatchers("/user/service-info").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("ADMIN")
                        // Admin only for listing all users
                        .requestMatchers(HttpMethod.GET, "/user/get-all-users").hasRole("ADMIN")
                        // Allow self or admin for GET, PUT, PATCH /api/users/{id}
                        .requestMatchers(HttpMethod.GET, "/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/user/**").hasAnyRole("USER", "ADMIN"))
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(CustomCorsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }

    private CorsConfigurationSource CustomCorsConfigurationSource() {
            return new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Collections.singletonList("*"));
                    configuration.setAllowedOrigins(Collections.singletonList("*")); // Replace with your frontend URL"*");
                    configuration.setAllowedMethods(Collections.singletonList("*")); // Replace with your allowed HTTP methods ("GET", "POST", "PUT", "DELETE");
                    configuration.setAllowedHeaders(Collections.singletonList("*")); // Replace it with your allowed headers
                    configuration.setAllowCredentials(true); // Set to true if you want to allow credentials (e.g., cookies)
                    configuration.setExposedHeaders(List.of("Authorization")); // Replace with your exposed headers
                    configuration.setMaxAge(3600L); // Set the maximum age for preflight requests to 3600 seconds
                    return configuration;
                }
            };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
