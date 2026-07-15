package com.postgamelab.user;

public record LoginUserResponse(
        String token,
        AuthenticatedUserResponse user
) {
    public static LoginUserResponse from(
            String token,
            User user
    ) {
        return new LoginUserResponse(
                token,
                AuthenticatedUserResponse.from(user)
        );
    }
}