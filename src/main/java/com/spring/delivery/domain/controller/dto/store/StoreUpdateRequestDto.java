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
    private boolean open_status;
    private LocalTime start_time;
    private LocalTime end_time;
}
