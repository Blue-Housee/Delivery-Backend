package com.spring.delivery.domain.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "p_delivery_address")
public class DeliveryAddress extends BaseEntity {
    @Id
    @UuidGenerator
    private UUID id;
    private String address;
    private String request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public DeliveryAddress(String address, String request, User user) {
        this.address = address;
        this.request = request;
        this.user = user;
    }

    public void update(String address){
        this.address=address;
    }
}
