package com.ocms.user.dto;

import com.ocms.user.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String contact,
        Role role,
        LocalDateTime createdAt
) {
}
