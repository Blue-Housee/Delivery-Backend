package com.spring.delivery.domain.controller.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequestDto {
    private String username;
    private String email;
    private String newPassword;
    private String originPassword; // 기존 password를 넣어야 내용을 바꿀 수 있도록 함
}
