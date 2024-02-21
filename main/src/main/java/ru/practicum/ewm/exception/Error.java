package ru.practicum.ewm.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Value
@Builder
public class Error {
    int status;

    String reason;

    String message;

    LocalDateTime timestamp;

    List<String> errors;
}
