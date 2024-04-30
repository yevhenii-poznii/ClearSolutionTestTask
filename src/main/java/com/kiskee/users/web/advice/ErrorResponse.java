package com.kiskee.users.web.advice;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String status,
        Map<String, String> errors,
        Instant timestamp) {
}