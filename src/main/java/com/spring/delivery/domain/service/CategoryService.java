package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.CategoryRequestDto;
import com.spring.delivery.domain.domain.entity.Category;
import com.spring.delivery.domain.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ApiResponseDto<UUID> createCategory(CategoryRequestDto requestDto) {
        // 중복 체크
        if (categoryRepository.findByName(requestDto.getName()).isPresent()) {
            return ApiResponseDto.fail(400, "카테고리가 이미 존재합니다.", null);
        }

        // 카테고리 생성
        Category category = requestDto.toEntity();
        categoryRepository.save(category);

        // 성공 응답 반환
        return ApiResponseDto.success(category.getId());
    }

}
