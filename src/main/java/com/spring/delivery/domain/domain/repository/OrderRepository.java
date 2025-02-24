package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUserIdAndOrderStatus(Long userId, String orderStatus, Pageable pageable);
}
