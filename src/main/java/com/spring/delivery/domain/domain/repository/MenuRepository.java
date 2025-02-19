package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface MenuRepository extends JpaRepository<Menu, UUID> {

    // 단건 조회
    @Query("SELECT m FROM Menu m WHERE m.id = :menuId AND m.public_status = true")
    Optional<Menu> findActiveMenuById(@Param("menuId") UUID menuId);

    // 메뉴 전체 리스트 조회
    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.public_status = true")
    Page<Menu> findActiveMenusByStoreId(@Param("storeId") UUID storeId, Pageable pageable);

}
