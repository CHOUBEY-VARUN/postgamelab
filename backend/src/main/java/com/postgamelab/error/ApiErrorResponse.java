package com.postgamelab.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldValidationError> fieldErrors
) {
    public ApiErrorResponse {
        fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
    }

    public static ApiErrorResponse of(
            int status,
            String code,
            String message,
            String path
    ) {
        return new ApiErrorResponse(
                Instant.now(),
                status,
                code,
                message,
                path,
                List.of()
        );
    }

    public static ApiErrorResponse withFieldErrors(
            int status,
            String code,
            String message,
            String path,
            List<FieldValidationError> fieldErrors
    ) {
        return new ApiErrorResponse(
                Instant.now(),
                status,
                code,
                message,
                path,
                fieldErrors
        );
    }
}
