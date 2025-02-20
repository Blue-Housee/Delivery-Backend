package com.spring.delivery.infra.exception;

public class GeminiApiException extends GeminiException {

    // api 호출 실패, 500
    public GeminiApiException(String message) {
        super(message);
    }
}
