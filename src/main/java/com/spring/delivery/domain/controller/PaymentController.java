package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.payment.PaymentResponseDto;
import com.spring.delivery.domain.domain.entity.Payment;
import com.spring.delivery.domain.domain.repository.PaymentRepository;
import com.spring.delivery.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponseDto> getPayment(
            @PathVariable UUID orderId
    ) {
        ApiResponseDto<PaymentResponseDto> paymentDto = paymentService.getPayment(orderId);
        return ResponseEntity.ok(paymentDto.getData());
    }

}
