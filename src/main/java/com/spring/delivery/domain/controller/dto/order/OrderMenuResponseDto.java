package com.spring.delivery.domain.controller.dto.order;

import com.spring.delivery.domain.domain.entity.Menu;
import com.spring.delivery.domain.domain.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderMenuResponseDto {
    private Order order;
    private List<Menu> menuList;

    public static OrderMenuResponseDto from(Order order, List<Menu> menus) {

            return new OrderMenuResponseDto(order, menus);
    }
}
