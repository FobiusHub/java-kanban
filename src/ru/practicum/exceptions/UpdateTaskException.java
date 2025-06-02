package ru.practicum.exceptions;

public class UpdateTaskException extends RuntimeException {
    private final String message;

    public UpdateTaskException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}