package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.Category;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {
    Optional<StoreCategory> findByStoreIdAndCategoryId(UUID storeId, UUID categoryId);
}
