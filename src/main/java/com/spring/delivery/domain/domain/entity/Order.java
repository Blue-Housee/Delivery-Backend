package com.spring.delivery.domain.domain.entity;

import com.spring.delivery.domain.controller.dto.OrderRequestDto;
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

    @OneToOne(mappedBy = "order")
    private Payment payment;

    @OneToMany(mappedBy = "order")
    private List<MenuOrder> menuOrderList = new ArrayList<>();

    private Order(User user, String order_status, Long total_price, Payment payment) {
        this.user = user;
        this.order_status = order_status;
        this.total_price = total_price;
        this.payment = payment;

    }

    public static Order createOrder(OrderRequestDto orderRequestDto) {
        return new Order(
                orderRequestDto.getUserId(),
                orderRequestDto.getOrderType(),
                orderRequestDto.getTotalPrice(),
                orderRequestDto.getPaymentId()
        );
    }
}
