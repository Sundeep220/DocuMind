package com.sundeep.user.user_service.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "user-service")
public record UserServiceInfoDTO(String message, Map<String, String> contactDetails) {
}
