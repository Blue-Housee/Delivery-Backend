package com.spring.delivery.domain.controller.dto;

import com.spring.delivery.domain.domain.entity.Category;
import lombok.Getter;

@Getter
public class CategoryRequestDto {
    private String name;

    // Category 엔티티로 변환하는 메서드
    public Category toEntity() {
        return Category.of(name); // 정적 팩토리 메서드를 사용하여 Category 생성
    }
}
