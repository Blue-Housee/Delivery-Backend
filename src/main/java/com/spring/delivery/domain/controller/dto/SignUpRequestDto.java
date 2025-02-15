package com.spring.delivery.domain.controller.dto;

import com.spring.delivery.domain.domain.entity.enumtype.Role;
import lombok.Getter;
import lombok.ToString;

@Getter
public class SignUpRequestDto {
    private String username;
    private String email;
    private String password;
    private Role role;
    private String adminToken;
}
