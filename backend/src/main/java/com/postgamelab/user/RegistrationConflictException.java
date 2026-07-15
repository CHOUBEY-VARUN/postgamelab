package com.postgamelab.user;

public class RegistrationConflictException extends RuntimeException {

    public RegistrationConflictException() {
        super("An account with this username or email address already exists.");
    }
}