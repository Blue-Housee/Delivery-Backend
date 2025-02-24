package com.spring.delivery.domain.controller.dto.category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryRequestDto {
    private String name;
}