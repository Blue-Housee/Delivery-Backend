package com.spring.delivery.domain.controller.dto;

import com.spring.delivery.domain.domain.entity.Payment;
import com.spring.delivery.domain.domain.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderRequestDto {
    // 메뉴 아이디
    private Long menuId;

    // 유저 아이디
    private User userId;

    // 주문 타입
    private String orderType;

    // 결제 아이디
    private Payment paymentId;

    // 총 금액
    private Long totalPrice;
}
