package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.SignUpRequestDto;
import com.spring.delivery.domain.controller.dto.SignUpResponseDto;
import com.spring.delivery.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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


}
