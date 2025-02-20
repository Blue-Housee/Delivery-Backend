package com.spring.delivery.domain.controller.dto;

import com.spring.delivery.domain.domain.entity.MenuOrder;
import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.Payment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private UUID orderId;
    private Long userId;
    private String orderStatus;
    private String address;
    private String orderType;
    private LocalDateTime createdAt;
    private String createdBy;
    private Long totalPrice;
    private Payment paymentId;
    private List<MenuOrder> menuOrders;

    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUser().getId();
        this.orderStatus = order.getOrder_status();
        this.address = order.getAddress();
        this.createdAt = order.getCreatedAt();
        this.createdBy = order.getCreatedBy();
        this.orderType = order.getOrder_type();
        this.totalPrice = order.getTotal_price();
        this.paymentId = order.getPayment();
    }

    public static OrderResponseDto from(Order order) {
        return new OrderResponseDto(order);
    }

}
