package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.user.SignUpRequestDto;
import com.spring.delivery.domain.controller.dto.user.SignUpResponseDto;
import com.spring.delivery.domain.controller.dto.user.UserResponseDto;
import com.spring.delivery.domain.controller.dto.user.UserUpdateRequestDto;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.service.UserService;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/health") //체크용
    private ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("health");
    }

    @PostMapping("/user/signUp") //회원가입
    private ResponseEntity<ApiResponseDto> signUp(@RequestBody SignUpRequestDto requestDto) {

        User createdUser = userService.signup(requestDto);

        return ResponseEntity
                .created(URI.create("/user/" + createdUser.getId()))
                .body(
                        ApiResponseDto.success(
                                SignUpResponseDto
                                        .builder()
                                        .userId(createdUser.getId())
                                        .build()
                        )
                );
    }

    @GetMapping("/user/{id}")
    private ResponseEntity<ApiResponseDto> getUser(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userService.getUser(id, userDetails);
        return ResponseEntity
                .ok(
                        ApiResponseDto.success(
                                UserResponseDto
                                        .builder()
                                        .userId(user.getId())
                                        .username(user.getUsername())
                                        .email(user.getEmail())
                                        .build()
                        )
                );
    }

    @PatchMapping("/user/{id}")
    private ResponseEntity<ApiResponseDto> updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userService.updateUser(id, requestDto, userDetails);
        return ResponseEntity
                .ok(
                        ApiResponseDto.success(
                                UserResponseDto
                                        .builder()
                                        .userId(user.getId())
                                        .username(user.getUsername())
                                        .email(user.getEmail())
                                        .build()
                        )
                );
    }

}
