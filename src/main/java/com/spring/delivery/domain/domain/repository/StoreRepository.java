package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.repository.search.StoreSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID>, StoreSearch {
    Page<Store> findByDeletedAtIsNull(Pageable pageable); // pageable을 인자로 받는 메서드 추가
}

