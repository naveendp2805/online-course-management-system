package com.ocms.auth.dto;

import com.ocms.user.enums.Role;

public record LoginResponse(

        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String name,
        String email,
        Role role

) {
}
