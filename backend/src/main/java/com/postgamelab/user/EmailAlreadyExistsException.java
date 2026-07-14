package com.postgamelab.user;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("This email address is already registered.");
    }
}