package com.spring.delivery.domain.controller.dto.store;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class StoreRequestDto {
    private String name;
    private List<UUID> categoryIds;
    private String address;
    private String tel;
    private boolean openStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
