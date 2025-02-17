package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.OrderRequestDto;
import com.spring.delivery.domain.controller.dto.OrderResponseDto;
import com.spring.delivery.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    // 의존성 주입
    private final OrderService orderService;

    // 주문 생성 controller
    @PostMapping("/")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> createOrder(
            @RequestBody OrderRequestDto orderRequestDto
    ){
        log.info(orderRequestDto);
        ApiResponseDto<OrderResponseDto> orderResponseDto = orderService.createOrder(orderRequestDto);
        return ResponseEntity.status(orderResponseDto.getStatus()).body(orderResponseDto);
    }


}
