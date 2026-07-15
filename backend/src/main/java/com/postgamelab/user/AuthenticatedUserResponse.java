package com.postgamelab.user;

import java.util.UUID;

public record AuthenticatedUserResponse(
        UUID id,
        String username,
        String email
) {
    public static AuthenticatedUserResponse from(User user) {
        return new AuthenticatedUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}