package com.postgamelab.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Username is required.")
        @Size(
                min = 3,
                max = 40,
                message = "Username must be between 3 and 40 characters."
        )
        @Pattern(
                regexp = "^[A-Za-z0-9_]+$",
                message = "Username may only contain letters, numbers, and underscores."
        )
        String username,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be a valid email address.")
        @Size(
                max = 255,
                message = "Email must not exceed 255 characters."
        )
        String email,

        @NotBlank(message = "Password is required.")
        @Size(
                min = 8,
                max = 72,
                message = "Password must be between 8 and 72 characters."
        )
        @Pattern(
                regexp = "^[\\x20-\\x7E]+$",
                message = "Password may only contain printable characters."
        )
        String password
) {
}