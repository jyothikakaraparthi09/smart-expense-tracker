package com.jyothi.smartexpensetracker.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int responseCode,
        LocalDateTime timestamp
) {
}
