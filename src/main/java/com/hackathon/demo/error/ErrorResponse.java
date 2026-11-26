package com.hackathon.demo.error;

import java.time.Instant;
import java.util.List;

/**
 * Body of every error response.
 *
 * @param fieldErrors invalid request fields; empty when not applicable
 */
public record ErrorResponse(Instant timestamp, int status, String error, String message, String path,
                            List<FieldError> fieldErrors) {

    public record FieldError(String field, String message) {
    }
}
