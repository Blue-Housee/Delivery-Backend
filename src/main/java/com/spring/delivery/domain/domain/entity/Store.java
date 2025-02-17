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
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<StoreCategory> storeCategories = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Menu> menus = new ArrayList<>();

    // 프라이빗 생성자
    private Store(String name, String address, String tel, boolean openStatus,
                  LocalDateTime startTime, LocalDateTime endTime, User user) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.open_status = openStatus;
        this.start_time = startTime;
        this.end_time = endTime;
        this.user = user;
    }

    // 정적 팩토리 메서드
    public static Store of(String name, String address, String tel, boolean openStatus,
                           LocalDateTime startTime, LocalDateTime endTime, User user) {
        return new Store(name, address, tel, openStatus, startTime, endTime, user);
    }
}