package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressRequestDto;
import com.spring.delivery.domain.service.DeliveryAddressService;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
