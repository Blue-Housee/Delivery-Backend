package com.spring.delivery.infra.exception;

public class GeminiTimeoutException extends GeminiException {

    // 응답시간초과, 504
    public GeminiTimeoutException(String message) {
        super(message);
    }
}
