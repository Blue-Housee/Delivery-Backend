package com.spring.delivery.domain.controller.dto.user;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserPageResponseDto {
    private int page;
    private int size;
    private int total;

    private List<UserResponseDto> users;
}
