package ru.practicum.ewm.exception;

public class AlreadyAvailableException extends RuntimeException {
    public AlreadyAvailableException(String message) {
        super(message);
    }
}
