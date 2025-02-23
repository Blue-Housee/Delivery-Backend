package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.user.*;
import com.spring.delivery.domain.service.UserService;
import com.spring.delivery.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입 API")
    @PostMapping("/user/signUp") //회원가입
    private ResponseEntity<ApiResponseDto<SignUpResponseDto>> signUp(@RequestBody @Valid SignUpRequestDto requestDto, BindingResult bindingResult) {

        // validation 예외처리
        raiseValidationException(bindingResult);
        SignUpResponseDto signUpResponseDto = userService.signup(requestDto);

        return ResponseEntity
                .created(URI.create("/user/" + signUpResponseDto.getUserId()))
                .body(ApiResponseDto.success(201, signUpResponseDto));
    }

    @Operation(summary = "User 단건 조회 API")
    @GetMapping("/user/{id}")
    private ResponseEntity<ApiResponseDto<UserDetailsResponseDto>> getUser(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserDetailsResponseDto userDetailsResponseDto = userService.getUser(id, userDetails);
        return ResponseEntity
                .ok(ApiResponseDto.success(userDetailsResponseDto));
    }

    @Operation(summary = "User search API")
    @GetMapping("/user")
    private ResponseEntity<ApiResponseDto<UserPageResponseDto>> searchUsers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size, //기본값 10
            @RequestParam(value = "orderby", defaultValue = "createdAt") String criteria,
            @RequestParam(value = "sort", defaultValue = "DESC") String sort,
            @RequestParam(value = "username", required = false) String username
    ) {
        // client 에서 1페이지 요청하면 0페이지를 반환하기 위해 page-1로 설정.
        UserPageResponseDto userPageResponseDto = userService.searchUsers(userDetails, page-1, size, criteria, sort, username);
        return ResponseEntity
                .ok(ApiResponseDto.success(userPageResponseDto));
    }

    @Operation(summary = "User 변경 API")
    @PatchMapping("/user/{id}")
    private ResponseEntity<ApiResponseDto<UserDetailsResponseDto>> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // validation 예외처리
        raiseValidationException(bindingResult);

        UserDetailsResponseDto userDetailsResponseDto = userService.updateUser(id, requestDto, userDetails);
        return ResponseEntity
                .ok(ApiResponseDto.success(userDetailsResponseDto));
    }

    @Operation(summary = "User 삭제 API")
    @DeleteMapping("/user/{id}")
    private ResponseEntity<ApiResponseDto<UserDeleteResponseDto>> deleteUser(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserDeleteResponseDto userDeleteResponseDto = userService.deleteUser(id, userDetails);
        return ResponseEntity
                .ok(ApiResponseDto.success(userDeleteResponseDto));
    }


    private static void raiseValidationException(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0){
            for(FieldError fieldError : fieldErrors){
                throw new IllegalArgumentException(fieldError.getDefaultMessage());
            }
        }
    }

}
