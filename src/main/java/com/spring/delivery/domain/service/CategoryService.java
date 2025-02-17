package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.*;
import com.spring.delivery.domain.controller.dto.category.CategoryListResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryRequestDto;
import com.spring.delivery.domain.controller.dto.category.CategoryUpdateResponseDto;
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
            return ApiResponseDto.fail(400, "카테고리가 이미 존재합니다.");
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

    @Transactional
    public ApiResponseDto<CategoryUpdateResponseDto> updateCategory(UUID categoryId, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        // 이름 중복 확인
        if (categoryRepository.findByName(requestDto.getName()).isPresent()) {
            return ApiResponseDto.fail(400, "이미 존재하는 카테고리 이름입니다.");
        }

        // 카테고리 이름 업데이트
        category.updateName(requestDto.getName());

        // 변경 사항을 즉시 DB에 반영하여 updatedAt 최신화
        categoryRepository.flush();

        // 업데이트 후 카테고리 응답 반환
        return ApiResponseDto.success(new CategoryUpdateResponseDto(category.getId(), category.getName(), category.getUpdatedAt()));
    }

}
