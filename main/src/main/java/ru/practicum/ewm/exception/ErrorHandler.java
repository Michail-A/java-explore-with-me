package ru.practicum.ewm.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleNotFound(final RuntimeException e) {
        return Error.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .reason(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of(printStackTrace(e)))
                .build();
    }

    @ExceptionHandler({AlreadyAvailableException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error handleAlreadyExists(final RuntimeException e) {
        return Error.builder()
                .status(HttpStatus.CONFLICT.value())
                .reason(HttpStatus.CONFLICT.getReasonPhrase())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of(printStackTrace(e)))
                .build();
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleBadRequest(final RuntimeException e) {
        return Error.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of(printStackTrace(e)))
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleMethodArgumentNotValid(final Exception e) {
        return Error.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of(printStackTrace(e)))
                .build();
    }

    private static String printStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
