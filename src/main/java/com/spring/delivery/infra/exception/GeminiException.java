package com.spring.delivery.infra.exception;

public class GeminiException extends RuntimeException {
    public GeminiException(String message) {
        super(message);
    }
}
