package ru.practicum.exceptions;

public class ManagerSaveException extends RuntimeException {
    private final String message;

    public ManagerSaveException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
