package com.spring.delivery.domain.controller.dto.store;

import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
public class StoreDetailResponseDto {
    private UUID storeId; // 가게 ID
    private String name; // 가게 이름
    private String address; // 가게 주소
    private String tel; // 전화번호
    private boolean openStatus; // 운영 상태
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private List<String> categories; // 카테고리 리스트

    public StoreDetailResponseDto(UUID storeId, String name, String address, String tel,
                                  boolean openStatus, LocalTime startTime, LocalTime endTime,
                                  List<String> categories) {
        this.storeId = storeId;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.openStatus = openStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.categories = categories;
    }
}


