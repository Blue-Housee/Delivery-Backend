package com.spring.delivery.domain.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponseDto {
    private String message;
    private String accessToken;
}
