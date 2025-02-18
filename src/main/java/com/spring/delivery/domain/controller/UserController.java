package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.user.*;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.service.UserService;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
                                UserDetailsResponseDto
                                        .builder()
                                        .userId(user.getId())
                                        .username(user.getUsername())
                                        .email(user.getEmail())
                                        .build()
                        )
                );
    }

    @GetMapping("/user")
    private ResponseEntity<ApiResponseDto> getAllUsers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size //기본값 10
    ) {
        // client 에서 1페이지 요청하면 0페이지를 반환하기 위해 page-1로 설정.
        Page<User> userList = userService.getAllUsers(userDetails, page-1, size);
        return ResponseEntity
                .ok(
                        ApiResponseDto.success(
                                UserPageResponseDto.builder()
                                        .page(userList.getNumber() + 1)
                                        .size(userList.getSize())
                                        .total(userList.getTotalPages())
                                        .users(
                                                //리스트 형태로 넣기
                                                userList.stream()
                                                        .map(user -> UserResponseDto.builder()
                                                                .userId(user.getId())
                                                                .username(user.getUsername())
                                                                .email(user.getEmail())
                                                                .role(user.getRole())
                                                                .deleted((user.getDeletedAt() != null))
                                                                .build()
                                                        )
                                                        .collect(Collectors.toList())
                                        )
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
                                UserDetailsResponseDto
                                        .builder()
                                        .userId(user.getId())
                                        .username(user.getUsername())
                                        .email(user.getEmail())
                                        .build()
                        )
                );
    }

    @DeleteMapping("/user/{id}")
    private ResponseEntity<ApiResponseDto> deleteUser(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userService.deleteUser(id, userDetails);
        return ResponseEntity
                .ok(
                        ApiResponseDto.success(
                                UserDeleteResponseDto
                                        .builder()
                                        .userId(user.getId())
                                        .build()
                        )
                );
    }

}
