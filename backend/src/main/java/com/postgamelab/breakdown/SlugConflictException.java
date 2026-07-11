package com.postgamelab.breakdown;

public class SlugConflictException extends RuntimeException {

    public SlugConflictException() {
        super("A breakdown with this slug already exists.");
    }
}
