package ru.practicum.exceptions;

public class ManagerLoadException extends RuntimeException {
    private final String message;

    public ManagerLoadException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}