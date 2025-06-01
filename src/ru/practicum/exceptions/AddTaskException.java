package ru.practicum.exceptions;

public class AddTaskException extends RuntimeException {
    private final String message;

    public AddTaskException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}