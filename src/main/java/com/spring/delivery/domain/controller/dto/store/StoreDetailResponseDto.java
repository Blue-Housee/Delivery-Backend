package com.spring.delivery.domain.controller.dto.store;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class StoreDetailResponseDto {
    private UUID storeId; // 가게 ID
    private String name; // 가게 이름
    private String address; // 가게 주소
    private String tel; // 전화번호
    private boolean openStatus; // 운영 상태
    private LocalDateTime start_time; // 시작 시간
    private LocalDateTime end_time; // 종료 시간
    private List<String> categories; // 카테고리 리스트

    public StoreDetailResponseDto(UUID storeId, String name, String address, String tel,
                                  boolean openStatus, LocalDateTime start_time, LocalDateTime end_time,
                                  List<String> categories) {
        this.storeId = storeId;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.openStatus = openStatus;
        this.start_time = start_time;
        this.end_time = end_time;
        this.categories = categories;
    }
}


