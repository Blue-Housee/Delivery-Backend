package com.spring.delivery.domain.controller.dto.store;

import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
public class StoreUpdateResponseDto {
    private UUID id;           // 가게 ID
    private String name;            // 가게 이름
    private List<UUID> categoryIds; // 카테고리 IDs
    private String address;         // 가게 주소
    private String tel;             // 전화번호
    private LocalTime startTime;       // 오픈 시간
    private LocalTime endTime;         // 종료 시간

    public StoreUpdateResponseDto(UUID id, String name, List<UUID> categoryIds, String address,
                                  String tel, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.name = name;
        this.categoryIds = categoryIds;
        this.address = address;
        this.tel = tel;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
