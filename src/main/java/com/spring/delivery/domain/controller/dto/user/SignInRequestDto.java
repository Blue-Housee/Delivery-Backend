package com.spring.delivery.domain.controller.dto.user;

import lombok.Getter;

@Getter
public class SignInRequestDto {
    private String username;
    private String password;
}
