package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {
}
