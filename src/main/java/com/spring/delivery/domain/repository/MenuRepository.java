package com.spring.delivery.domain.repository;

import com.spring.delivery.domain.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface MenuRepository extends JpaRepository<Menu, UUID> {
}
