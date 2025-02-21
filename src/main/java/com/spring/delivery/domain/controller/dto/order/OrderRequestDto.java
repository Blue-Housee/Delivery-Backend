package com.spring.delivery.domain.controller.dto.order;

import com.spring.delivery.domain.domain.entity.Payment;
import com.spring.delivery.domain.domain.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class OrderRequestDto {
    // 메뉴정보 리스트 => 메뉴의 uuid 와 수량을 리스트의 형태로 받아옴
    private List<Map<UUID, Long>> menuInfo;

    // 유저 아이디
    private User userId;

    // 주문 타입
    private String orderType;

    // 결제 아이디
    private Payment paymentId;

    // 총 금액
    private Long totalPrice;

    // 수정용 메뉴 id
    private List<Map<UUID, Long>> updateMenuIds;

    // 배송지
    private String address;

    // 카드번호
    private String cardNumber;
}
