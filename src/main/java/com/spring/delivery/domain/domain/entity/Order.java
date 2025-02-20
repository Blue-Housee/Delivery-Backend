package com.spring.delivery.domain.domain.entity;

import com.spring.delivery.domain.controller.dto.order.OrderRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "p_order")
public class Order extends BaseEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String order_status;

    private String order_type;

    private Long total_price;

    private String address;

    @OneToMany(mappedBy = "order")
    private List<MenuOrder> menuOrderList = new ArrayList<>();

    private Order(User userId, String address ,String order_type, Long total_price) {
        this.user = userId;
        this.address = address;
        this.order_type = order_type;
        this.total_price = total_price;
    }

    public static Order createOrder(OrderRequestDto orderRequestDto) {
        return new Order(
                orderRequestDto.getUserId(),
                orderRequestDto.getOrderType(),
                orderRequestDto.getAddress(),
                orderRequestDto.getTotalPrice()
        );
    }

    public static void update(Order order, OrderRequestDto orderRequestDto) {
        if (orderRequestDto.getTotalPrice() != null) { order.total_price = orderRequestDto.getTotalPrice(); }
        if (orderRequestDto.getUserId() != null){ order.user = orderRequestDto.getUserId(); }
        if (orderRequestDto.getOrderType() != null){ order.order_type = orderRequestDto.getOrderType(); }
        if (orderRequestDto.getAddress() != null){ order.address = orderRequestDto.getAddress(); }
    }

}
