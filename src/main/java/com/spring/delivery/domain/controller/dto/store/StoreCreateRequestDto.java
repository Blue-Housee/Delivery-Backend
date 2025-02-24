package com.spring.delivery.domain.controller.dto.store;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
public class StoreCreateRequestDto {
    private String name;
    private List<UUID> categoryIds;
    private String address;
    private String tel;
    private boolean openStatus;
    private LocalTime startTime;
    private LocalTime endTime;


    public StoreCreateRequestDto(String name, List<UUID> categoryIds, String address, String tel,
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
