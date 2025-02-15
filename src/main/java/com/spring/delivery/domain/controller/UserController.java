package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.*;
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
    private ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto requestDto) {

        Long createdUserId = userService.signup(requestDto);

        return ResponseEntity
                .created(URI.create("/user/" + createdUserId))
                .body(
                        SignUpResponseDto
                                .builder()
                                .message("회원가입이 완료되었습니다.")
                                .userId(createdUserId)
                                .build()
                );
    }

    @GetMapping("/user/{id}")
    private ResponseEntity<UserResponseDto> getUser(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userService.getUser(id, userDetails);
        return ResponseEntity
                .ok(
                        UserResponseDto
                                .builder()
                                .userId(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .build()
                );
    }

}
