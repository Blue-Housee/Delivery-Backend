package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryDeleteResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryListResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryRequestDto;
import com.spring.delivery.domain.controller.dto.category.CategoryUpdateResponseDto;
import com.spring.delivery.domain.domain.entity.Category;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.CategoryRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 권한 체크 메서드
    private ApiResponseDto<Void> checkUserAuthority(UserDetailsImpl userDetails) {
        if (userDetails == null) {

            return ApiResponseDto.fail(HttpStatus.UNAUTHORIZED.value(), "인증 정보가 없습니다.");
        }

        Role currentUserRole = userDetails.getUser().getRole();

        // ROLE_MASTER 권한 체크
        if (currentUserRole != Role.MASTER) {

            return ApiResponseDto.fail(HttpStatus.FORBIDDEN.value(), "권한이 없습니다.");
        }

        return ApiResponseDto.success(null); // 권한이 있는 경우
    }

    // 중복 체크 메서드 (ApiResponseDto로 반환)
    private ApiResponseDto<Void> checkDuplicateCategoryName(String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            return ApiResponseDto.fail(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 카테고리 이름입니다.");
        }

        return ApiResponseDto.success(null);
    }

    // 카테고리 조회 (Category 객체 반환)
    private Category findCategoryById(UUID categoryId) {

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));
    }

    // 카테고리 생성
    public ApiResponseDto<UUID> createCategory(UserDetailsImpl userDetails, CategoryRequestDto requestDto) {
        ApiResponseDto<Void> authorityCheck = checkUserAuthority(userDetails);
        if (authorityCheck.getStatus() != HttpStatus.OK.value()) {
            return ApiResponseDto.fail(authorityCheck.getStatus(), authorityCheck.getMessage());
        }

        ApiResponseDto<Void> duplicateCheck = checkDuplicateCategoryName(requestDto.getName());
        if (duplicateCheck.getStatus() != HttpStatus.OK.value()) {
            return ApiResponseDto.fail(duplicateCheck.getStatus(), duplicateCheck.getMessage());
        }

        Category category = Category.of(requestDto.getName());
        categoryRepository.save(category);

        return ApiResponseDto.success(category.getId());
    }

    // 모든 카테고리 조회
    @Transactional(readOnly = true)
    public ApiResponseDto<Page<CategoryListResponseDto>> getAllCategories(UserDetailsImpl userDetails, int page, int size, String sortBy, boolean isAsc) {
        ApiResponseDto<Void> authorityCheck = checkUserAuthority(userDetails);
        if (authorityCheck.getStatus() != HttpStatus.OK.value()) {
            return ApiResponseDto.fail(authorityCheck.getStatus(), authorityCheck.getMessage());
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Category> categories = categoryRepository.findAll(pageable);

        return ApiResponseDto.success(categories.map(category -> new CategoryListResponseDto(
                category.getId(),
                category.getName(),
                category.getDeletedAt()
        )));
    }

    // 카테고리 업데이트
    @Transactional
    public ApiResponseDto<CategoryUpdateResponseDto> updateCategory(UserDetailsImpl userDetails, UUID categoryId, CategoryRequestDto requestDto) {
        ApiResponseDto<Void> authorityCheck = checkUserAuthority(userDetails);
        if (authorityCheck.getStatus() != HttpStatus.OK.value()) {
            return ApiResponseDto.fail(authorityCheck.getStatus(), authorityCheck.getMessage());
        }

        Category category = findCategoryById(categoryId); // Category 객체 직접 받기

        // 업데이트 하기 전, 바꿀 이름이 중복인지 확인
        if (!category.getName().equals(requestDto.getName())) {
            ApiResponseDto<Void> duplicateCheck = checkDuplicateCategoryName(requestDto.getName());
            if (duplicateCheck.getStatus() != HttpStatus.OK.value()) {
                return ApiResponseDto.fail(duplicateCheck.getStatus(), duplicateCheck.getMessage());
            }
        }

        category.updateName(requestDto.getName());
        categoryRepository.flush();

        return ApiResponseDto.success(new CategoryUpdateResponseDto(category.getId(), category.getName(), category.getUpdatedAt()));
    }

    // 카테고리 삭제
    @Transactional
    public ApiResponseDto<CategoryDeleteResponseDto> deleteCategory(UserDetailsImpl userDetails, UUID categoryId) {
        ApiResponseDto<Void> authorityCheck = checkUserAuthority(userDetails);
        if (authorityCheck.getStatus() != HttpStatus.OK.value()) {
            return ApiResponseDto.fail(authorityCheck.getStatus(), authorityCheck.getMessage());
        }

        Category category = findCategoryById(categoryId);

        if (category.getDeletedAt() != null) {
            return ApiResponseDto.fail(HttpStatus.BAD_REQUEST.value(), "이미 삭제된 카테고리입니다.");
        }

        category.delete(userDetails.getUsername());

        return ApiResponseDto.success(new CategoryDeleteResponseDto(
                "카테고리가 삭제(숨김 처리)되었습니다.",
                category.getDeletedAt()
        ));
    }
}
