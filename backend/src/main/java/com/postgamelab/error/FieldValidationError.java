package com.postgamelab.error;

public record FieldValidationError(
        String field,
        String message
) {
}
