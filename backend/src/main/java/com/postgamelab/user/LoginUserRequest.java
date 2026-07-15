package com.postgamelab.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginUserRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be a valid email address.")
        @Size(
                max = 255,
                message = "Email must not exceed 255 characters."
        )
        String email,

        @NotBlank(message = "Password is required.")
        @Size(
                max = 72,
                message = "Password must not exceed 72 characters."
        )
        @Pattern(
                regexp = "^[\\x20-\\x7E]+$",
                message = "Password may only contain printable characters."
        )
        String password
) {
}