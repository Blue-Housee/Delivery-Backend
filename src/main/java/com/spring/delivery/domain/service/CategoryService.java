package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.*;
import com.spring.delivery.domain.domain.entity.Category;
import com.spring.delivery.domain.domain.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Category category = Category.of(requestDto.getName());
        categoryRepository.save(category);

        // 성공 응답 반환
        return ApiResponseDto.success(category.getId());
    }

    @Transactional(readOnly = true)
    public Page<CategoryListResponseDto> getAllCategories(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Category> categories = categoryRepository.findAll(pageable);

        return categories.map(category -> new CategoryListResponseDto(
                category.getId(),
                category.getName(),
                category.getDeletedAt()
        ));
    }



}
