package com.spring.delivery.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "p_payment")
public class Payment extends BaseEntity{
    @Id
    @UuidGenerator
    private UUID id;

    private String cardnumber;
    private boolean payments_status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") //외래키 설정
    private Order order;

    public static Payment createPayment(Order order, String cardNumber) {
        Payment payment = new Payment();
        payment.order = order;
        payment.cardnumber = cardNumber;
        payment.payments_status = true;
        return payment;
    }
}
