package com.spring.delivery.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "p_store")
public class Store extends BaseEntity {
    @Id
    @UuidGenerator
    private UUID id;

    private String name;

    private String address;

    private String tel;

    private boolean open_status;

    private LocalDateTime start_time;

    private LocalDateTime end_time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 유저 ID를 참조하는 외래 키 컬럼
    private User user;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<StoreCategory> storeCategories = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Menu> menus = new ArrayList<>();
}