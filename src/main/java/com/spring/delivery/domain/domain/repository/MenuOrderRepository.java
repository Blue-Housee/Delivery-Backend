package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.MenuOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuOrderRepository extends JpaRepository<MenuOrder, Long> {
}
