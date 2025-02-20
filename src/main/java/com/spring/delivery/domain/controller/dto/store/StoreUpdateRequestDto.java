package com.spring.delivery.domain.controller.dto.store;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class StoreUpdateRequestDto {
    private String name;
    private List<UUID> categoryIds;
    private String address;
    private String tel;
    private boolean openStatus;
    private LocalTime startTime;
    private LocalTime endTime;
}
