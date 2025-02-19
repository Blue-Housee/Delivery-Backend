package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.OrderRequestDto;
import com.spring.delivery.domain.controller.dto.OrderResponseDto;
import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.repository.OrderRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public ApiResponseDto<OrderResponseDto> createOrder(OrderRequestDto orderRequestDto) {
        // 새로운 order 생성
        Order order = Order.createOrder(orderRequestDto);
        // DB에 저장
        orderRepository.save(order);

        // 새로운 주문생성 성공 반환 데이터 return
        return ApiResponseDto.success(OrderResponseDto.from(order));
    }

    @Transactional
    public ApiResponseDto<OrderResponseDto> updateOrder(UUID id, OrderRequestDto orderRequestDto, UserDetailsImpl userDetails) {

        // MASTER, MANAGER만 사용가능 -> userDetails에서 role확인
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        // user의 권한이 MASETR , MANAGER 라면 TRUE값 리턴
        boolean isMangerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER") || auth.getAuthority().equals("ROLE_MASTER"));

        // isMangerOrMaster 가 False 라면 return 권한없음
        if (!isMangerOrMaster) {
            return ApiResponseDto.fail(403, "주문을 수정할 권한이 없습니다.");
        }

        // 들어온 주문 id가 주문 DB에 있는지 확인
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ApiResponseDto.fail(404, "해당 주문은 존재하지 않습니다.");
        }

        // 주문이 존재한다면 주문 수정
        Order.update(order, orderRequestDto);

        // 수정후 성공 메세지 return
        return ApiResponseDto.success(null);
    }

    @Transactional
    public ApiResponseDto<OrderResponseDto> deleteOrder(UUID id, UserDetailsImpl userDetails) {
        // MASTER, MANAGER만 사용가능 -> userDetails에서 role확인
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        // user의 권한이 MASETR , MANAGER 라면 TRUE값 리턴
        boolean isMangerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER") || auth.getAuthority().equals("ROLE_MASTER"));

        // isMangerOrMaster 가 False 라면 return 권한없음
        if (!isMangerOrMaster) {
            return ApiResponseDto.fail(403, "주문을 수정할 권한이 없습니다.");
        }

        // 들어온 주문 id가 주문 DB에 있는지 확인
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ApiResponseDto.fail(404, "해당 주문은 존재하지 않습니다.");
        }

        // 주문이 존재한다면 주문삭제
        order.delete(userDetails.getUser().getUsername());

        return ApiResponseDto.success(null);
    }
}
