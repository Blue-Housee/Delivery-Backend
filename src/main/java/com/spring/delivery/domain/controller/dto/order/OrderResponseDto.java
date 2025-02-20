package com.spring.delivery.domain.controller.dto.order;

import com.spring.delivery.domain.domain.entity.MenuOrder;
import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.Payment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    private List<MenuOrder> menuOrders;

    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUser().getId();
        this.orderStatus = order.getOrderStatus();
        this.address = order.getAddress();
        this.createdAt = order.getCreatedAt();
        this.createdBy = order.getCreatedBy();
        this.orderType = order.getOrderType();
        this.totalPrice = order.getTotalPrice();
    }

    public static OrderResponseDto from(Order order) {
        return new OrderResponseDto(order);
    }

}
