package com.company.api.error;

public class AppForbiddenException extends RuntimeException {
    public AppForbiddenException(String message) {
        super(message);
    }
}
