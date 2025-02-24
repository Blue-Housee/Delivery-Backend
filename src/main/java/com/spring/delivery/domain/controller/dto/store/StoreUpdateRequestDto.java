package com.spring.delivery.domain.controller.dto.store;

import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
public class StoreUpdateRequestDto {
    private String name;
    private List<UUID> categoryIds;
    private String address;
    private String tel;
    private boolean openStatus;
    private LocalTime startTime;
    private LocalTime endTime;

    // 모든 필드를 초기화하는 생성자
    public StoreUpdateRequestDto(String name, List<UUID> categoryIds, String address, String tel,
                                 boolean openStatus, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        this.categoryIds = categoryIds;
        this.address = address;
        this.tel = tel;
        this.openStatus = openStatus;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
