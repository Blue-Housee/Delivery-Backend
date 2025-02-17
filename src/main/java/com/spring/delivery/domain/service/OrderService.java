package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.MenuResponseDto;
import com.spring.delivery.domain.controller.dto.OrderRequestDto;
import com.spring.delivery.domain.controller.dto.OrderResponseDto;
import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public ApiResponseDto<OrderResponseDto> createOrder(OrderRequestDto orderRequestDto) {
        Order order = Order.createOrder(orderRequestDto);
        orderRepository.save(order);

        return ApiResponseDto.success(OrderResponseDto.from(order));
    }
}
