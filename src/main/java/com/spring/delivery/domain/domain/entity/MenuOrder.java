package com.spring.delivery.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "p_menu_order")
public class MenuOrder extends BaseEntity {
    @Id
    @UuidGenerator
    private UUID id;

    private Long amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private MenuOrder(Order order, Menu menu, Long amount) {
        this.amount = amount;
        this.menu = menu;
        this.order = order;
    }

    public static MenuOrder create(Order order, Menu menu, Long amount) {
        return new MenuOrder(order, menu, amount);
    }

    public static void update(MenuOrder updateMenuOrder, Long updateValue) {
        updateMenuOrder.amount = updateValue;
    }
}
