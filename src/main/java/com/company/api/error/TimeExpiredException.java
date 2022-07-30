package com.company.api.error;

public class TimeExpiredException extends RuntimeException{
    public TimeExpiredException(String message) {
        super(message);
    }
}
