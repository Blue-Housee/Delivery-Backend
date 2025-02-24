package com.spring.delivery.domain.controller.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDeleteResponseDto {
    private Long userId;
}
