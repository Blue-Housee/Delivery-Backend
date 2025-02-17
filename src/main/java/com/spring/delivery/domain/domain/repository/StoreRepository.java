package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
}
