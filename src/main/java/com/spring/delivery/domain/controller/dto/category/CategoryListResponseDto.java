package com.spring.delivery.domain.controller.dto.category;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CategoryListResponseDto {
    private UUID id;
    private String name;
    private LocalDateTime deletedAt;

    public CategoryListResponseDto(UUID id, String name, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.deletedAt = deletedAt;
    }
}
