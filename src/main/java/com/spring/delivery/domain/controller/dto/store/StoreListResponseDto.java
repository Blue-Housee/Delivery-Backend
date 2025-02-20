package com.spring.delivery.domain.controller.dto.store;

import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
public class StoreListResponseDto {
    private UUID storeId; // Store ID
    private String name; // 가게 이름
    private String address; // 가게 주소
    private String tel; // 전화번호
    private boolean openStatus; // 운영 상태
    private List<String> categories; // 카테고리 리스트
    private LocalTime startTime;
    private LocalTime endTime;

    public StoreListResponseDto(UUID storeId, String name, String address, String tel,
                                boolean openStatus, List<String> categories,
                                LocalTime startTime, LocalTime endTime) {
        this.storeId = storeId;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.openStatus = openStatus;
        this.categories = categories;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
