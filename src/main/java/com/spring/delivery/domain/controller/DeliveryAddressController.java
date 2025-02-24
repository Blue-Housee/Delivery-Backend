package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressUpdateRequestDto;
import com.spring.delivery.domain.service.DeliveryAddressService;
import com.spring.delivery.global.security.UserDetailsImpl;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    //주문지 생성
    @PostMapping("/address")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<ApiResponseDto> createDeliveryAddress(@Valid @RequestBody DeliveryAddressRequestDto dto,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(deliveryAddressService.createDeliveryAddress(dto,userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }

    //주문지 수정
    @PatchMapping("/address/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<ApiResponseDto> updateDeliveryAddress(@PathVariable UUID id,
                                                                @Valid @RequestBody DeliveryAddressUpdateRequestDto dto,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(deliveryAddressService.updateDeliveryAddress(id, dto,userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }

    //주문지 검색
    @GetMapping("/address/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<ApiResponseDto> selectDeliveryAddress(@PathVariable UUID id,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(deliveryAddressService.selectDeliveryAddress(id, userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }

    @GetMapping("/address")
    public ResponseEntity<ApiResponseDto> selectAllDeliveryAddress(@AuthenticationPrincipal UserDetailsImpl userDetails){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(deliveryAddressService.selectAllDeliveryAddress( userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }

    //주문지 제거
    @DeleteMapping("/address/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<ApiResponseDto> deleteDeliveryAddress(@PathVariable UUID id,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(deliveryAddressService.deleteDeliveryAddress(id, userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }
}
