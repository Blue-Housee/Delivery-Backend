package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressUpdateRequestDto;
import com.spring.delivery.domain.service.DeliveryAddressService;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    @PostMapping("/address")
    public ResponseEntity<ApiResponseDto> createDeliveryAddress(@RequestBody DeliveryAddressRequestDto dto,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(deliveryAddressService.createDeliveryAddress(dto,userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }

    @PatchMapping("/address/{id}")
    public ResponseEntity<ApiResponseDto> updateDeliveryAddress(@PathVariable UUID id,
                                                                @RequestBody DeliveryAddressUpdateRequestDto dto,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(deliveryAddressService.updateDeliveryAddress(id, dto,userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }

    @GetMapping("/address/{id}")
    public ResponseEntity<ApiResponseDto> selectDeliveryAddress(@PathVariable UUID id,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(deliveryAddressService.selectDeliveryAddress(id, userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<ApiResponseDto> deleteDeliveryAddress(@PathVariable UUID id,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(deliveryAddressService.deleteDeliveryAddress(id, userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }
}
