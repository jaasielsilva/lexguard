package com.jaasielsilva.lexguard.exception;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {

    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final List<String> messages;
}
