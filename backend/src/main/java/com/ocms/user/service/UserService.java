package com.ocms.user.service;

import com.ocms.exception.AdminRegistrationNotAllowedException;
import com.ocms.exception.EmailAlreadyExistsException;
import com.ocms.exception.ResourceNotFoundException;
import com.ocms.user.dto.RegisterUserRequest;
import com.ocms.user.dto.UpdateProfileRequest;
import com.ocms.user.dto.UserResponse;
import com.ocms.user.entity.User;
import com.ocms.user.enums.Role;
import com.ocms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerUser(RegisterUserRequest request) {

        if (request.getRole() == Role.ADMIN) {
            throw new AdminRegistrationNotAllowedException("Admin registration is not allowed");
        }

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setContact(request.getContact().trim());
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getContact(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUserProfile(
            String email,
            UpdateProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        user.setName(request.name().trim());
        user.setContact(request.contact().trim());

        User updatedUser = userRepository.save(user);

        return toUserResponse(updatedUser);
    }

    private UserResponse toUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getContact(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

}
