package com.sundeep.user.user_service.service;

import com.sundeep.user.user_service.dto.UserPatchRequest;
import com.sundeep.user.user_service.dto.UserResponseDTO;
import com.sundeep.user.user_service.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;

public interface UserService {
    UserResponseDTO getUserProfile(String jwtToken);
    UserResponseDTO getUserById(Long id, String requesterEmail);

    UserResponseDTO updateUserDetails(Long id, String requesterEmail, UserUpdateRequest request);
    UserResponseDTO patchUser(Long id, String requesterEmail, UserPatchRequest request);
    Page<UserResponseDTO> getAllUsers(String requesterEmail, int page, int size, String role);
    void deleteUserById(Long id, String requesterEmail);

    UserResponseDTO getUserByEmail(String email, String name);
}
