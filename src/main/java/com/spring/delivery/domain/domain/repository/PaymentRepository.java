package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
