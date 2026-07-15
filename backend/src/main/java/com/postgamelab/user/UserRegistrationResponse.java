package com.postgamelab.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegistrationResponse(
        UUID id,
        String username,
        String email,
        LocalDateTime createdAt
) {
    public static UserRegistrationResponse from(User user) {
        return new UserRegistrationResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}