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

    // 응답 시간 초과 (504)
    @ExceptionHandler(GeminiTimeoutException.class)
    public ResponseEntity<ApiResponseDto<String>> handleTimeoutException(GeminiTimeoutException e) {
        log.warn("AI 추천 서비스 응답 시간 초과 - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ApiResponseDto.fail(504, "AI 추천 서비스 응답 시간이 초과되었습니다. 잠시 후 다시 시도해주세요."));
    }

    // Gemini API 서버 다운(503)
    @ExceptionHandler(GeminiServiceUnavailableException.class)
    public ResponseEntity<ApiResponseDto<String>> handleServiceUnavailable(GeminiServiceUnavailableException e) {
        log.warn("AI 추천 서비스 다운 - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDto.fail(503, "현재 AI 추천 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해 주세요."));
    }

    // Gemini API 호출 실패 (500)
    @ExceptionHandler(GeminiApiException.class)
    public ResponseEntity<ApiResponseDto<String>> handleApiException(GeminiApiException e) {
        log.error("AI 추천 서비스 오류 - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail(500, "AI 추천 서비스 요청 중 오류가 발생했습니다."));
    }

    // 모든 Gemini 예외 처리 (500)
    @ExceptionHandler(GeminiException.class)
    public ResponseEntity<ApiResponseDto<String>> handleGenericGeminiException(GeminiException e) {
        log.error("Gemini API 예외 발생 - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail(500, "Gemini 서비스에서 오류가 발생했습니다."));
    }

}
