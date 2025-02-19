package com.spring.delivery.domain.controller.dto.store;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class StoreListResponseDto {
    private UUID storeId; // Store ID
    private String name; // 가게 이름
    private String address; // 가게 주소
    private String tel; // 전화번호
    private boolean open_status; // 운영 상태
    private List<String> categories; // 카테고리 리스트
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간

    public StoreListResponseDto(UUID storeId, String name, String address, String tel,
                                boolean open_status, List<String> categories,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.open_status = open_status;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
