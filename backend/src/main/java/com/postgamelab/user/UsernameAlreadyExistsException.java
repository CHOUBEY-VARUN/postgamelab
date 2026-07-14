package com.postgamelab.user;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException() {
        super("This username is already registered.");
    }
}