package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.Menu;
import com.spring.delivery.infra.gemini.Gemini;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface MenuRepository extends JpaRepository<Menu, UUID> {

    // 단건 조회
    @Query("SELECT m FROM Menu m WHERE m.id = :menuId AND m.publicStatus = true")
    Optional<Menu> findActiveMenuById(@Param("menuId") UUID menuId);

    // 메뉴 전체 리스트 조회 가게별
    @Query("SELECT m FROM Menu m WHERE m.store.id = :storeId AND m.publicStatus = true")
    Page<Menu> findActiveMenusByStoreId(@Param("storeId") UUID storeId, Pageable pageable);

    // 메뉴 전체 리스트 조회
    @Query("SELECT m FROM Menu m WHERE m.publicStatus = true")
    Page<Menu> findActiveMenus(Pageable pageable);

    // 모든 메뉴 내역 중, 특정 키워드를 포함한 결과
    Page<Menu> findByNameContaining(String keyword, Pageable pageable);

    // 가게별 메뉴 내역 조회(전체)
    Page<Menu> findByStoreId(UUID storeId, Pageable pageable);

    // 가게별, 특정 키워드를 포함한 결과
    Page<Menu> findByStoreIdAndNameContaining(UUID storeId, String keyword, Pageable pageable);


}
