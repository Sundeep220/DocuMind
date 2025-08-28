package com.sundeep.document_service.client;

import com.sundeep.document_service.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "${user.service.url}") // adjust host/port as needed
public interface UserServiceClient {

    @GetMapping("/user/profile")
    ResponseEntity<UserResponseDTO> getUserProfile(@RequestHeader("Authorization") String jwtToken);
}
