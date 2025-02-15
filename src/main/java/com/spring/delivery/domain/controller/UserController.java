package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.SignUpRequestDto;
import com.spring.delivery.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private ResponseEntity<String> signUp(@RequestBody SignUpRequestDto requestDto) {

        userService.signup(requestDto);
        return ResponseEntity.ok("회원가입 성공");

    }



}
