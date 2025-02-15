package com.spring.delivery.domain.controller.dto;

import lombok.Getter;

@Getter
public class SignInRequestDto {
    private String email;
    private String password;
}
