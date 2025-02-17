package com.spring.delivery.domain.controller.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private Long userId;
    private String username;
    private String email;
}
