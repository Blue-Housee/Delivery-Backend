package com.spring.delivery.infra.exception;

public class GeminiServiceUnavailableException extends GeminiException {

    // 서버다운, 503
    public GeminiServiceUnavailableException(String message) {
        super(message);
    }
}
