package com.ocms.auth.service;

import com.ocms.auth.dto.LoginRequest;
import com.ocms.auth.dto.LoginResponse;
import com.ocms.user.entity.User;
import com.ocms.user.repository.UserRepository;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        // 1. Normalize email
        String email = request.email()
                .trim()
                .toLowerCase();

        // 2. Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        // 3. Verify password
        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        // 4. Generate JWT
        String token = jwtService.generateToken(user);

        // 5. Return authentication response
        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationInSeconds(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
