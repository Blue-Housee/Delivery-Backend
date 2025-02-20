package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.MenuOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface MenuOrderRepository extends JpaRepository<MenuOrder, UUID> {
    List<MenuOrder> findByOrderId(UUID orderId);
}
