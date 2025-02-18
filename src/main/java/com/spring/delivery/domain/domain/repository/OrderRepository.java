package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
