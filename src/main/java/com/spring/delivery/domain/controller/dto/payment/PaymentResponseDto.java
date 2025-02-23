package com.spring.delivery.domain.controller.dto.payment;

import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.Payment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PaymentResponseDto {

    private String cardNumber;
    private Order order;
    private boolean paymentStatus;


    public PaymentResponseDto(Order order, String cardNumber, boolean paymentStatus) {
        this.order = order;
        this.cardNumber = cardNumber;
        this.paymentStatus = paymentStatus;
    }


    public static PaymentResponseDto from(Payment payment) {
        return new PaymentResponseDto(payment.getOrder(), payment.getCardNumber(), payment.isPaymentStatus());
    }
}
