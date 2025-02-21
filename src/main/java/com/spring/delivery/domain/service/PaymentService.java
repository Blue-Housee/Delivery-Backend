package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.payment.PaymentResponseDto;
import com.spring.delivery.domain.domain.entity.Payment;
import com.spring.delivery.domain.domain.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public ApiResponseDto<PaymentResponseDto> getPayment(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        return ApiResponseDto.success(PaymentResponseDto.from(payment));
    }
}
