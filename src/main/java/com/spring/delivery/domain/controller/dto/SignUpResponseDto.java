package com.spring.delivery.domain.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponseDto {
    private String message;
    private Long userId;
}
