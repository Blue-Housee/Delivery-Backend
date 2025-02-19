package com.spring.delivery.domain.controller.dto.category;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CategoryUpdateResponseDto {
    private UUID id;
    private String name;
    private LocalDateTime updatedAt;

    public CategoryUpdateResponseDto(UUID id, String name, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.updatedAt = updatedAt;
    }
}

