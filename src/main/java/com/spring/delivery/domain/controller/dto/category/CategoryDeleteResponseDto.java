package com.spring.delivery.domain.controller.dto.category;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CategoryDeleteResponseDto {
    private String message;
    private LocalDateTime deletedAt;

    public CategoryDeleteResponseDto(String message, LocalDateTime deletedAt) {
        this.message = message;
        this.deletedAt = deletedAt;
    }
}
