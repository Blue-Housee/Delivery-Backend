package com.spring.delivery.domain.controller.dto.user;

import com.spring.delivery.domain.domain.entity.enumtype.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class SignUpRequestDto {
    @NotBlank(message = "username은 필수 입력값입니다.")
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[a-z0-9]+$")
    private String username;

    @NotBlank(message = "email은 필수 입력값입니다.")
    @Email
    private String email;

    @NotBlank(message = "password는 필수 입력값입니다.")
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^[A-Za-z0-9_!#$%&'*+/=?`{|}~^.-]+$")
    private String password;

    @NotNull(message = "role는 필수 입력값입니다.")
    private Role role;

    private String adminToken;
}
