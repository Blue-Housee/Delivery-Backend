package com.spring.delivery.domain.domain.entity;

import com.spring.delivery.domain.controller.dto.store.StoreUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalTime;
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

    private boolean openStatus;

    private LocalTime startTime;

    private LocalTime endTime;

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
                  LocalTime startTime, LocalTime endTime, User user) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.openStatus = openStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
    }

    // 정적 팩토리 메서드
    public static Store of(String name, String address, String tel, boolean openStatus,
                           LocalTime startTime, LocalTime endTime, User user) {
        return new Store(name, address, tel, openStatus, startTime, endTime, user);
    }

    public void update(StoreUpdateRequestDto requestDto) {
        this.name = requestDto.getName() != null ? requestDto.getName() : this.name;
        this.address = requestDto.getAddress() != null ? requestDto.getAddress() : this.address;
        this.tel = requestDto.getTel() != null ? requestDto.getTel() : this.tel;
        this.openStatus = requestDto.isOpenStatus();  // open_status는 null 체크가 필요 없음
        this.startTime = requestDto.getStartTime() != null ? requestDto.getStartTime() : this.startTime;
        this.endTime = requestDto.getEndTime() != null ? requestDto.getEndTime() : this.endTime;
    }
}