package com.spring.delivery.domain.controller.dto.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PaymentRequestDto {
    // 주문 id
    private String userId;

    // 가격
    private String price;

    // 카드번호
    private String cardNumber;
}
