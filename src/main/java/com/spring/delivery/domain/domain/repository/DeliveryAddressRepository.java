package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, UUID> {
    List<DeliveryAddress> findByUser_Id(Long userId);
    DeliveryAddress findByUser_IdAndAddress(Long userId, String address);
}
