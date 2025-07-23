package com.sundeep.user.user_service.service.impl;

import com.sundeep.user.user_service.dto.UserPatchRequest;
import com.sundeep.user.user_service.dto.UserResponseDTO;
import com.sundeep.user.user_service.dto.UserUpdateRequest;
import com.sundeep.user.user_service.entity.User;
import com.sundeep.user.user_service.exceptions.UserNotFoundException;
import com.sundeep.user.user_service.repo.UserRepository;
import com.sundeep.user.user_service.service.UserService;
import com.sundeep.user.user_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO getUserProfile(String jwtToken) {
        String email = JwtUtil.getEmailFromToken(jwtToken);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found with email: " + email)
        );

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Override
    public UserResponseDTO getUserById(Long id, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail).orElseThrow(
                () -> new UserNotFoundException("Requester not found with email: " + requesterEmail)
        );
        User target = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with given id: " + id));

        if (!requester.getRole().equals(User.Role.ADMIN) && !requester.getId().equals(target.getId())) {
            throw new RuntimeException("Access denied");
        }
        return new UserResponseDTO(
                target.getId(),
                target.getName(),
                target.getEmail(),
                target.getRole().name()
        );
    }

    @Override
    public UserResponseDTO getUserByEmail(String email, String name) {
        User requester = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("Requester not found with email: " + email)
        );

        User target = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("Requester not found with email: " + email)
        );

        if (!requester.getRole().equals(User.Role.ADMIN) && !requester.getId().equals(target.getId())) {
            throw new RuntimeException("Access denied");
        }

        return new UserResponseDTO(
                target.getId(),
                target.getName(),
                target.getEmail(),
                target.getRole().name()
        );
    }

    @Override
    public UserResponseDTO updateUserDetails(Long id, String requesterEmail, UserUpdateRequest request) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with given id: " + id));

        if (!requester.getRole().equals(User.Role.ADMIN) && !requester.getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

//        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User updated = userRepository.save(user);

        return new UserResponseDTO(updated.getId(), updated.getName(), updated.getEmail(), updated.getRole().name());
    }

    @Override
    public UserResponseDTO patchUser(Long id, String requesterEmail, UserPatchRequest request) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with given id: " + id));

        if (!requester.getRole().equals(User.Role.ADMIN) && !requester.getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        switch (request.getField().toLowerCase()) {
            case "name" -> user.setName(request.getValue());
            case "password" -> user.setPassword(passwordEncoder.encode(request.getValue()));
            case "email" -> user.setEmail(request.getValue());
            default -> throw new RuntimeException("Invalid field for patch");
        }

        User patched = userRepository.save(user);

        return new UserResponseDTO(patched.getId(), patched.getName(), patched.getEmail(), patched.getRole().name());
    }

    @Override
    public void deleteUserById(Long id, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        User target = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with given id: " + id));

        if (!requester.getRole().equals(User.Role.ADMIN) && !requester.getId().equals(target.getId())) {
            throw new RuntimeException("Access denied. Only admin or the user can delete this account.");
        }


        userRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(String requesterEmail, int page, int size, String role) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        if (!requester.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Access denied. Admins only.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;

        if (role != null && !role.isBlank()) {
            try {
                User.Role roleEnum = User.Role.valueOf(role.toUpperCase());
                users = userRepository.findByRole(roleEnum, pageable);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + role);
            }
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(u -> new UserResponseDTO(u.getId(), u.getName(), u.getEmail(), u.getRole().name()));
    }

}
