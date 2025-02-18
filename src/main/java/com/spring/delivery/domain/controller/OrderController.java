package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.OrderRequestDto;
import com.spring.delivery.domain.controller.dto.OrderResponseDto;
import com.spring.delivery.domain.service.OrderService;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    // 의존성 주입
    private final OrderService orderService;

    // 주문 생성 controller
    @PostMapping("/")
    // 주문의 관한 dto를 반환하기 위해 반환 데이터를 responseDto로 선언
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> createOrder(
            // 생성에 필요한 데이터를 requestbody를 통해 받아옴
            @RequestBody OrderRequestDto orderRequestDto
    ){
        // 잘들어왔니??
        log.info(orderRequestDto);
        System.out.println("hi");
        // orderservice에 주문생성기능 사용후 client로 return
        ApiResponseDto<OrderResponseDto> orderResponseDto = orderService.createOrder(orderRequestDto);
        return ResponseEntity.status(orderResponseDto.getStatus()).body(orderResponseDto);
    }

    // 주문 수정 controller
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> updateOrder(
            // uuid인 주문 아이디를 path variable 방식으로 받아옴
            @PathVariable UUID id,
            // 수정에 필요한 데이터를 requestBody를 통해 받아옴
            @RequestBody OrderRequestDto orderRequestDto,
            // 수정의 기능은 master와 manager만 가능
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        // 잘들어왔니..?
        log.info(orderRequestDto);
        log.info(userDetails);

        // orderservice에 주문수정기능 사용후 client로 return
        ApiResponseDto<OrderResponseDto> orderResponseDto = orderService.updateOrder(id, orderRequestDto, userDetails);
        return ResponseEntity.status(orderResponseDto.getStatus()).body(orderResponseDto);
    }



}
