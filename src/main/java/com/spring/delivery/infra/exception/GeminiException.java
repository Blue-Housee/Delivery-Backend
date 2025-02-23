package com.spring.delivery.infra.exception;

import org.springframework.http.HttpStatus;

public class GeminiException extends RuntimeException {
    public GeminiException(String message) {
        super(message);
    }



}
