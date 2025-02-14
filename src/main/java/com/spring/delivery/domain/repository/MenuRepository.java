package com.spring.delivery.domain.repository;

import com.spring.delivery.domain.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.beans.JavaBean;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
