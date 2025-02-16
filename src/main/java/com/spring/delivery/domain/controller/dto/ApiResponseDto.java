package com.spring.delivery.domain.controller.dto;

import lombok.Getter;

@Getter
public class ApiResponseDto <T>{
    private static final String SUCCESS_MESSAGE ="요청이 성공적으로 처리되었습니다.";

    private int status;
    private String message;
    private T data;
    private T error;

    public ApiResponseDto(int status,String message, T data,T error) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.error=error;
    }

    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(200, SUCCESS_MESSAGE, data,null);  // default status 200
    }

    public static <T> ApiResponseDto<T> fail(int status, String message,T errors) {
        return new ApiResponseDto<>(status, message, null, errors);  // default status 200
    }

}
