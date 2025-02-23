package com.spring.delivery.infra.exception;


import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice(basePackages = "infra")
public class GeminiExceptionHandler {

    // 모든 Gemini 예외 처리 (500)
    @ExceptionHandler(GeminiException.class)
    public ResponseEntity<ApiResponseDto<String>> handleGeminiException(GeminiException e) {
        HttpStatus status = getStatus(e);

        if (status == HttpStatus.GATEWAY_TIMEOUT) {
            log.warn("AI 추천 서비스 응답 시간 초과 - {}", e.getMessage());
        } else if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            log.warn("AI 추천 서비스 다운 - {}", e.getMessage());
        } else {
            log.error("Gemini API 예외 발생 - {}", e.getMessage());
        }

        return ResponseEntity.status(status)
                .body(ApiResponseDto.fail(status.value(), e.getMessage()));
    }

    private HttpStatus getStatus(GeminiException e) {
        if (e instanceof GeminiTimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT; // 504
        } else if(e instanceof GeminiServiceUnavailableException) {
            return HttpStatus.SERVICE_UNAVAILABLE; // 503
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR; // 500
        }

    }

}
