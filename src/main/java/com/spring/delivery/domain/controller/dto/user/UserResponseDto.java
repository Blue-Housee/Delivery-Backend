package com.spring.delivery.domain.controller.dto.user;

import com.spring.delivery.domain.domain.entity.enumtype.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private boolean deleted;
}
