package com.spring.delivery.domain.exception;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final View error;

    public GlobalExceptionHandler(View error) {
        this.error = error;
    }

    // 중복된 사용자명 또는 이메일 등으로 발생하는 예외 처리 : 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseDto.fail(400, e.getMessage())
        );
    }

    // 접근 권한이 없는 경우 : 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponseDto.fail(403, e.getMessage())
        );
    }

    // 서버 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseDto.fail(500, e.getMessage())
        );
    }

    // 존재하지 않는 데이터에 대한 예외 처리
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponseDto> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseDto.fail(404, e.getMessage())
        );
    }

    //validation에 대한 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto> handleNotNullElementException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldErrors = bindingResult.getFieldError();
        String errorCode = fieldErrors.getCode(); //어노테이션명
        String fieldError =e.getFieldError().getDefaultMessage(); //메세지



        return ResponseEntity.status(e.getStatusCode()).body(
                ApiResponseDto.fail(e.getStatusCode().value(), "[" +errorCode+"] " +fieldError)
        );
    }

}